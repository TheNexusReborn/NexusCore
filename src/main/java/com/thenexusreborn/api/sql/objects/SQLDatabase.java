package com.thenexusreborn.api.sql.objects;

import com.stardevllc.observable.Property;
import com.thenexusreborn.api.sql.DatabaseRegistry;
import com.thenexusreborn.api.sql.interfaces.SQLDB;
import com.thenexusreborn.api.sql.objects.typehandlers.*;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * This class represents a Database<br>
 * Please be aware, that Databases must already exist, this library will not create databases<br>
 * Normal Database actions happen in their own connection to prevent memory leaks. <br>
 * In order to bulk push data, please use the queue() and flush() methods <br>
 * Please see the documentation for the get() and the save() methods for saving and loading data from the database<br>
 * All actions happen on the same thread they are called on, nothing is async. There is plans to add methods for this.
 */
public abstract class SQLDatabase implements SQLDB {
    protected Logger logger;
    protected String url, name, user, password;
    protected boolean primary;
    protected Set<Table> tables = new HashSet<>();
    protected Set<TypeHandler> typeHandlers = new HashSet<>();
    protected DatabaseRegistry registry;

    protected final LinkedList<Object> queue = new LinkedList<>();

    protected SQLDatabase() {
        this.typeHandlers.addAll(Set.of(new BooleanHandler(), new DoubleHandler(), new EnumHandler(), new FloatHandler(), new IntegerHandler(), new LongHandler(), new StringHandler(), new UUIDHander()));
    }

    public SQLDatabase(Logger logger, SQLProperties properties) {
        this();
        this.logger = logger;
        this.name = properties.getDatabaseName();
        this.user = properties.getUsername();
        this.password = properties.getPassword();
    }

    /**
     * @return The name of this database
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return The user of this database
     */
    @Override
    public String getUser() {
        return user;
    }

    /**
     * @return The password of this database
     */
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Registers a table to this database
     *
     * @param clazz The table to register
     */
    @Override
    public void registerClass(Class<?> clazz) {
        try {
            this.tables.add(new Table(this, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return All registered tables. This is Thread-Safe as it returns a copy of the Set
     */
    @Override
    public Set<Table> getTables() {
        return new HashSet<>(tables);
    }

    protected Object parseLinkedTableObject(Column column, Row row, String key) throws SQLException {
        Table linkedTable = column.getLinkedTable();
        if (linkedTable != null) {
            List<?> objects = get(linkedTable.getModelClass(), "id", row.getLong(key));
            if (objects.size() != 1) {
                return null;
            }

            return objects.getFirst();
        }

        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    protected <T> T createClassInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            return null;
        }
    }

    protected void handleAfterLoad(Table table, Object object) {
        Class<? extends ObjectHandler> handler = table.getHandler();
        if (handler != null) {
            try {
                Constructor<?> constructor = handler.getDeclaredConstructor(Object.class, SQLDatabase.class, Table.class);
                ObjectHandler o = (ObjectHandler) constructor.newInstance(object, this, table);
                o.afterLoad();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    protected <T> T parseObjectFromRow(Class<T> clazz, Row row) {
        Table table = getTable(clazz);
        T object = createClassInstance(clazz);

        if (object == null) {
            return null;
        }

        for (String key : row.getData().keySet()) {
            Column column = table.getColumn(key);
            if (column == null) {
                continue;
            }

            try {
                Object data = row.getObject(key);

                if (column.getLinkedTable() != null) {
                    data = parseLinkedTableObject(column, row, key);
                }

                if (data == null) {
                    continue;
                }

                Field field = column.getField();
                field.setAccessible(true);

                if (Property.class.isAssignableFrom(field.getType())) {
                    Object fieldValue = field.get(object);
                    Property<Object> property = (Property<Object>) fieldValue;
                    for (TypeHandler typeHandler : this.typeHandlers) {
                        if (typeHandler.matches(property.getTypeClass())) {
                            data = typeHandler.deserializer.deserialize(column, data);
                            break;
                        }
                    }
                    property.setValue(data);
                } else {
                    field.set(object, data);
                }
            } catch (Exception e) {
                logger.severe("Error while retrieving data from database: ");
                e.printStackTrace();
            }
        }

        handleAfterLoad(table, object);
        return object;
    }

    /**
     * Gets Objects of the class provided. This will still return a list even if it is just one<br>
     * The two arrays must match each other in both length and what you want to do. The index of the first one will map to the index of the second one.
     *
     * @param clazz   The class of the table
     * @param columns The Array of Columns to base the query on. This must match the values
     * @param values  The array of values based on columns. This must match the columns
     * @param <T>     The type of the table
     * @return The list of objects that Match. This should never return a null object. If there is nothing that matches, it will be an empty list.
     * @throws SQLException Any SQL errors that happen
     */
    @Override
    public <T> List<T> get(Class<T> clazz, String[] columns, Object[] values) throws SQLException {
        if (columns == null || values == null) {
            throw new IllegalArgumentException("Columns or Values are null");
        }

        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns has a size of " + columns.length + " and values has a size of " + values.length + ". They must be equal.");
        }

        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }

        Map<Column, Object> tableColumns = new HashMap<>();

        for (int i = 0; i < columns.length; i++) {
            Column column = table.getColumn(columns[i]);
            if (column != null) {
                tableColumns.put(column, values[i]);
            }
        }

        List<String> whereConditions = new ArrayList<>();
        tableColumns.forEach((column, value) -> whereConditions.add(column.getName() + "='" + value.toString() + "'"));

        StringBuilder sb = new StringBuilder("select * from " + table.getName() + " where ");
        for (int i = 0; i < whereConditions.size(); i++) {
            sb.append(whereConditions.get(i));
            if (i < whereConditions.size() - 1) {
                sb.append(" and ");
            }
        }

        sb.append(";");

        List<Row> rows = executeQuery(sb.toString());

        List<T> objects = new ArrayList<>();
        for (Row row : rows) {
            objects.add(parseObjectFromRow(clazz, row));
        }

        return objects;
    }

    /**
     * Gets objects based on a column and a value. This will still return a list even if it is just one that matches
     *
     * @param clazz      The class of the table
     * @param columnName The column to select based on
     * @param value      The value to select based on
     * @param <T>        The type of the table
     * @return The list of objects that match
     * @throws SQLException Any SQL errors
     */
    @Override
    public <T> List<T> get(Class<T> clazz, String columnName, Object value) throws SQLException {
        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }

        Column column = table.getColumn(columnName);
        if (column == null) {
            return new ArrayList<>();
        }

        List<Row> rows = executeQuery("select * from " + table.getName() + " where " + column.getName() + "='" + value + "';");

        List<T> objects = new ArrayList<>();
        for (Row row : rows) {
            objects.add(parseObjectFromRow(clazz, row));
        }
        return objects;
    }

    /**
     * Gets all objects based on a type
     *
     * @param clazz The class of the table
     * @param <T>   The type of the table
     * @return The list of objects that match
     * @throws SQLException Any SQL errors
     */
    @Override
    public <T> List<T> get(Class<T> clazz) throws SQLException {
        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }

        List<T> objects = new ArrayList<>();

        List<Row> rows = executeQuery("select * from " + table.getName());
        for (Row row : rows) {
            objects.add(parseObjectFromRow(clazz, row));
        }

        return objects;
    }

    protected ObjectHandler handleBeforeSave(Table table, Object object) {
        Class<? extends ObjectHandler> handler = table.getHandler();
        if (handler != null) {
            try {
                Constructor<?> constructor = handler.getDeclaredConstructor(Object.class, SQLDatabase.class, Table.class);
                ObjectHandler objectHandler = (ObjectHandler) constructor.newInstance(object, this, table);
                objectHandler.beforeSave();
                return objectHandler;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected PushInfo generateObjectPushInfo(Object object) throws SQLException {
        Class<?> clazz = object.getClass();
        Table table = getTable(clazz);
        if (table == null) {
            return null;
        }

        ObjectHandler handler = handleBeforeSave(table, object);

        Map<String, Object> data = new HashMap<>();
        Column primaryColumn = table.getPrimaryKeyColumn();
        Object primaryKeyValue = null;
        boolean getGeneratedKeys = primaryColumn.isAutoIncrement();
        for (Column column : table.getColumns()) {
            Field field = column.getField();
            try {
                field.setAccessible(true);
                Object value = field.get(object);

                if (column.getCodec() != null) {
                    value = column.getCodec().encode(value);
                }

                if (column.getLinkedTable() != null && value != null) {
                    Column linkedPrimaryKeyColumn = column.getLinkedTable().getPrimaryKeyColumn();
                    save(value);
                    data.put(column.getName(), linkedPrimaryKeyColumn.getField().get(value));
                    continue;
                }

                if (column.getTypeHandler() != null) {
                    value = column.getTypeHandler().getSerializer().serialize(column, value);
                }

                if (value instanceof String str) {
                    str = str.replace("\\", "\\\\");
                    str = str.replace("'", "\\'");
                    value = str;
                }

                if (column.isAutoIncrement()) {
                    getGeneratedKeys = true;
                }

                if (column.isPrimaryKey()) {
                    primaryKeyValue = value;
                    continue;
                }

                data.put(column.getName(), value);
            } catch (IllegalAccessException e) {
            }
        }

        Iterator<Entry<String, Object>> iterator = data.entrySet().iterator();
        StringBuilder insertColumnBuilder = new StringBuilder(), insertValueBuilder = new StringBuilder(), updateBuilder = new StringBuilder();
        if (!primaryColumn.isAutoIncrement()) {
            insertColumnBuilder.append("`").append(primaryColumn.getName()).append("`").append(", ");
            insertValueBuilder.append("'").append(primaryKeyValue).append("'").append(", ");
        } else {
            Number number = (Number) primaryKeyValue;
            if (number.longValue() != 0) {
                insertColumnBuilder.append("`").append(primaryColumn.getName()).append("`").append(", ");
                insertValueBuilder.append("'").append(primaryKeyValue).append("'").append(", ");
            }
        }
        while (iterator.hasNext()) {
            Entry<String, Object> entry = iterator.next();
            insertColumnBuilder.append("`").append(entry.getKey()).append("`");
            updateBuilder.append("`").append(entry.getKey()).append("`").append("=");
            if (entry.getValue() != null) {
                insertValueBuilder.append("'").append(entry.getValue()).append("'");
                updateBuilder.append("'").append(entry.getValue()).append("'");
            } else {
                insertValueBuilder.append("null");
                updateBuilder.append("null");
            }

            if (iterator.hasNext()) {
                insertColumnBuilder.append(", ");
                insertValueBuilder.append(", ");
                updateBuilder.append(", ");
            }
        }
        
        String columns = insertColumnBuilder.toString();
        String values = insertValueBuilder.toString();
        
        if (columns.charAt(columns.length() - 2) == ',' && columns.charAt(columns.length() - 1) == ' ') {
            columns = columns.substring(0, columns.length() - 2);
        }
        
        if (values.charAt(values.length() - 2) == ',' && values.charAt(values.length() - 1) == ' ') {
            values = values.substring(0, values.length() - 2);
        }
        
        String update = "";
        if (!updateBuilder.isEmpty()) {
            update = " on duplicate key update " + updateBuilder;
        }
        
        String sql = "insert into " + table.getName() + " (" + columns + ") values (" + values + ") " + update + ";";
        return new PushInfo(sql, getGeneratedKeys, table, handler);
    }

    /**
     * Saves an object to the database while catching {@link SQLException}s
     *
     * @param object The object to save to the database
     */
    @Override
    public void saveSilent(Object object) {
        try {
            save(object);
        } catch (SQLException e) {
        }
    }

    /**
     * Saves an object to the database
     *
     * @param object The object to save
     * @throws SQLException Any SQL errors that happen
     */
    @Override
    public void save(Object object) throws SQLException {
        PushInfo pushInfo = generateObjectPushInfo(object);
        boolean getGeneratedKeys = pushInfo.isGenerateKeys();
        String sql = pushInfo.getSql();
        Table table = pushInfo.getTable();

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            if (getGeneratedKeys) {
                statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                ResultSet generatedKeys = statement.getGeneratedKeys();
                generatedKeys.next();
                for (Column column : table.getColumns()) {
                    if (column.isAutoIncrement()) {
                        updateAutoIncrement(column, object, generatedKeys);
                        break;
                    }
                }
            } else {
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            System.out.println(sql);
            throw e;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ObjectHandler objectHandler = pushInfo.getObjectHandler();
        if (objectHandler != null) {
            objectHandler.afterSave();
        }
    }

    private void updateAutoIncrement(Column column, Object object, ResultSet generatedKeys) throws SQLException, IllegalAccessException {
        Number number = (Number) column.getField().get(object);
        if (column.getType().equalsIgnoreCase("int")) {
            if (number.intValue() == 0) {
                column.getField().set(object, generatedKeys.getInt(1));
            }
        } else if (column.getType().equalsIgnoreCase("bigint")) {
            if (number.longValue() == 0) {
                column.getField().set(object, generatedKeys.getLong(1));
            }
        } else if (column.getType().equalsIgnoreCase("double")) {
            if (number.doubleValue() == 0) {
                column.getField().set(object, generatedKeys.getDouble(1));
            }
        } else if (column.getType().equalsIgnoreCase("float")) {
            if (number.floatValue() == 0) {
                column.getField().set(object, generatedKeys.getFloat(1));
            }
        }
    }

    /**
     * Deletes an object from the database while catching {@link SQLException}
     *
     * @param clazz The class of the table
     * @param id    The ID to delete. This is the the Primary Column value based on either auto-detection or the @ID annotation
     */
    @Override
    public int deleteSilent(Class<?> clazz, Object id) {
        try {
            return delete(clazz, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Deletes an object from the database while cactching {@link SQLException}
     *
     * @param object The object to delete
     */
    @Override
    public int deleteSilent(Object object) {
        try {
            return delete(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public int deleteSilent(Class<?> clazz, Object id, Object[] columns, Object[] values) {
        try {
            return delete(clazz, id, columns, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Deletes an Object from the database<br>
     * Note: This will throw an {@link IllegalArgumentException} if no table is found
     *
     * @param object The object to delete
     * @throws SQLException Any SQL Errors
     */
    @Override
    public int delete(Object object) throws SQLException {
        Table table = getTable(object.getClass());
        if (table == null) {
            throw new IllegalArgumentException("No table registered for class " + object.getClass());
        }

        Column primaryColumn = null;
        for (Column column : table.getColumns()) {
            if (column.isPrimaryKey()) {
                primaryColumn = column;
            }
        }

        try {
            Object id = primaryColumn.getField().get(object);
            return delete(object.getClass(), id);
        } catch (IllegalAccessException e) {
        }
        return 0;
    }
    
    @Override
    public int delete(Class<?> clazz, Object id) throws SQLException {
        return delete(clazz, id, null, null);
    }

    /**
     * Deletes an object from the database
     *
     * @param clazz The table class
     * @param id    The id to delete. This is the the Primary Column value based on either auto-detection or the @ID annotation
     * @throws SQLException Any SQL Errors
     */
    @Override
    public int delete(Class<?> clazz, Object id, Object[] columns, Object[] values) throws SQLException {
        Table table = getTable(clazz);
        if (table == null) {
            return 0;
        }

        Column primaryColumn = null;
        for (Column column : table.getColumns()) {
            if (column.isPrimaryKey()) {
                primaryColumn = column;
            }
        }
        
        List<String> additionalClauses = new ArrayList<>();
        if (columns != null && values != null && columns.length == values.length) {
            for (int i = 0; i < columns.length; i++) {
                additionalClauses.add(" and `" + columns[i] + "`='" + values[i] + "'");
            }
        }
        
        StringBuilder sql = new StringBuilder("delete from `" + table.getName() + "` where `" + primaryColumn.getName() + "`='" + id + "'");
        if (!additionalClauses.isEmpty()) {
            for (String clause : additionalClauses) {
                sql.append(clause);
            }
        }
        
        sql.append(";");

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql.toString());
        }
    }

    public int count(Class<?> clazz) throws SQLException {
        Table table = getTable(clazz);
        if (table == null) {
            return 0;
        }

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select count(*) as `count` from `" + table.getName() + "`;");
            resultSet.next();
            return resultSet.getInt("count");
        }
    }

    /**
     * Gets a registered table: Case Insensitive
     *
     * @param name The name of the table
     * @return The registered table or null if one does not exist
     */
    @Override
    public Table getTable(String name) {
        for (Table table : new ArrayList<>(this.tables)) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }

    /**
     * Gets a registered rable
     * Note: This will check against super classes if one is not found by the direct class provided
     *
     * @param clazz The class of the table
     * @return The registered table or null if one does not exist
     */
    @Override
    public Table getTable(Class<?> clazz) {
        for (Table table : new ArrayList<>(this.tables)) {
            if (table.getModelClass().equals(clazz)) {
                return table;
            }
        }

        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            return getTable(clazz.getSuperclass());
        }

        return null;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    protected Row parseRow(String sql) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return new Row(rs, this);
        }
    }

    protected boolean parsePreparedParmeters(PreparedStatement statement, Object... args) {
        if (args == null || args.length == 0) {
            return false;
        }

        try {
            if (statement.getParameterMetaData().getParameterCount() != args.length) {
                return false;
            }

            for (int i = 0; i < args.length; i++) {
                statement.setObject(i, args[i]); //Hopefully this will work, otherwise more things will be needed to make it work
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected Row parsePreparedRow(String sql, Object... args) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                ResultSet rs = statement.executeQuery();
                return new Row(rs, this);
            }
        }

        return null;
    }

    /**
     * Executes a given SQL Statement
     *
     * @param sql The SQL string
     * @throws SQLException Any SQL Errors
     */
    @Override
    public void execute(String sql) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(sql);
            throw e;
        }
    }

    /**
     * Executes a given Prepared SQL Statement
     *
     * @param sql  The SQL Statement
     * @param args The arguments for the prepared statement
     * @throws SQLException Any SQL Errors
     */
    @Override
    public void executePrepared(String sql, Object... args) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                statement.executeUpdate();
            }
        }
    }

    /**
     * Executes a query on the database
     * It is safe to use the data from the rows whenever as it is cached
     *
     * @param sql The query sql
     * @return The rows from the statement
     * @throws SQLException Any SQL Errors
     */
    @Override
    public List<Row> executeQuery(String sql) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                rows.add(new Row(resultSet, this));
            }
            return rows;
        }
    }

    /**
     * Executs a prepared query on the database
     *
     * @param sql  The SQL statement
     * @param args The arugments for the prepared statement
     * @return The list of rows from executing
     * @throws SQLException Any SQL Errors
     */
    @Override
    public List<Row> executePreparedQuery(String sql, Object... args) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            boolean value = parsePreparedParmeters(statement, args);
            if (value) {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    rows.add(new Row(resultSet, this));
                }
            }
            return rows;
        }
    }

    /**
     * Don't use
     *
     * @return Don't use
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Adds an object to the Queue.
     * This queue is to allow pushing many objects in a single connection
     *
     * @param object The object to add
     */
    @Override
    public void queue(Object object) {
        this.queue.add(object);
    }

    /**
     * Flushes the queue. This does catch SQL Exceptions
     */
    @Override
    public void flush() {
        if (!this.queue.isEmpty()) {
            try (Connection connection = getConnection()) {
                for (Object object : this.queue) {
                    PushInfo pushInfo = generateObjectPushInfo(object);
                    Statement statement = connection.createStatement();
                    if (pushInfo.isGenerateKeys()) {
                        statement.executeUpdate(pushInfo.getSql(), Statement.RETURN_GENERATED_KEYS);
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        generatedKeys.next();
                        for (Column column : pushInfo.getTable().getColumns()) {
                            if (column.isAutoIncrement()) {
                                updateAutoIncrement(column, object, generatedKeys);
                            }
                        }
                    } else {
                        statement.executeUpdate(pushInfo.getSql());
                    }

                    if (pushInfo.getObjectHandler() != null) {
                        pushInfo.getObjectHandler().afterSave();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SQLDatabase database = (SQLDatabase) o;
        return Objects.equals(name, database.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * @return The defined type handles.
     */
    @Override
    public Set<TypeHandler> getTypeHandlers() {
        Set<TypeHandler> typeHandlers = new HashSet<>(this.typeHandlers);
        typeHandlers.addAll(Set.of(new BooleanHandler(), new DoubleHandler(), new EnumHandler(), new FloatHandler(), new IntegerHandler(), new LongHandler(), new StringHandler(), new UUIDHander()));
        if (registry != null) {
            typeHandlers.addAll(registry.getTypeHandlers());
        }
        return typeHandlers;
    }

    /**
     * Adds a custom type handler specific to this database.
     *
     * @param handler The handler to add
     */
    @Override
    public void addTypeHandler(TypeHandler handler) {
        this.typeHandlers.add(handler);
    }

    /**
     * Sets the {@link DatabaseRegistry} that this is registered to.<br>
     * Note: The register methods will do this automatically
     *
     * @param registry The registry
     */
    @Override
    public void setRegistry(DatabaseRegistry registry) {
        this.registry = registry;
    }

    /**
     * @return The {@link DatabaseRegistry} that this database is registered to.
     */
    @Override
    public DatabaseRegistry getRegistry() {
        return registry;
    }
}
