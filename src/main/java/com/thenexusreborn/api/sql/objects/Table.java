package com.thenexusreborn.api.sql.objects;

import com.stardevllc.helper.ReflectionHelper;
import com.stardevllc.observable.Property;
import com.thenexusreborn.api.sql.annotations.ID;
import com.thenexusreborn.api.sql.annotations.column.ColumnCodec;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableHandler;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;

/**
 * Represents a Database Table.<br>
 * A table generates columns based on Class Fields. This library will take fields from parent classes as well.<br>
 * It is highly recommended to have a primary key field as the library allows you to update existing data for a specific object.<br>
 * You can have fields ignored using one of the following ways: The {@link ColumnIgnored} annotation, the {@code modifiers}; {@code static}, {@code transient}, and {@code final}<br>
 * Fields with types that extend from the {@link Collection} and {@link Map} classes are ignored as these are not yet supported, however if you have the field annotated with {@link ColumnCodec}, it will not ignore it. <br>
 * Fields that have classes that are registered as a table will link with that and will automatically load from that table. The column type will be the same as the Primary Key of the linked table.<br>
 * In order to use that functionality, the table must be registered to the database before this one is registered.<br>
 * If you want to have collections supported, you must use either a {@link SqlCodec} or an {@link ObjectHandler} or a combination of both<br>
 * You must have a field that is an {@link ID} column for it to work. You can technically use any object as it though. The two ways you can do it is from the ID annotation or using the {@link PrimaryKey} annotation. <br>
 * The ID annotation will automatically set it as the Primary Key and as an AutoIncrement if it an object type that this library supports as such.<br>
 * Note: Fields that are of type {@code long} or {@link Long} and named {@code id} are automatically set as the {@link ID} field. This is for compatibility with existing projects that I have.
 */
public class Table implements Comparable<Table> {
    private String name;
    private final Class<?> modelClass;
    private final Set<Column> columns = new TreeSet<>();
    private Class<? extends ObjectHandler> handler;
    private SQLDatabase database;
    private Column primaryKeyColumn;
    
    /**
     * Constructs a new table based on a Java Class
     * @param database The Database that this table is registered to.
     * @param modelClass The class to use
     */
    public Table(SQLDatabase database, Class<?> modelClass) {
        this.database = database;
        this.modelClass = modelClass;
    
        try {
            modelClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find default constructor for class " + modelClass.getName());
        }
    
        name = determineTableName(modelClass);
        handler = determineObjectHandler(modelClass);

        if (name == null) {
            name = modelClass.getSimpleName().toLowerCase();
        }
    
        for (Field field : ReflectionHelper.getClassFields(modelClass)) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(ColumnIgnored.class)) {
                continue;
            }
            
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            
            boolean isProperty = Property.class.isAssignableFrom(field.getType());
            
            if (Modifier.isFinal(field.getModifiers()) && !isProperty) {
                continue;
            }
    
            Class<?> type = field.getType();
            if (Collection.class.isAssignableFrom(type) && !field.isAnnotationPresent(ColumnCodec.class)) {
                continue;
            }
            
            if (Map.class.isAssignableFrom(type) && !field.isAnnotationPresent(ColumnCodec.class)) {
                continue;
            }
    
            Column column = new Column(this, field);
        
            if (column.getType() == null || column.getType().isEmpty()) {
                continue;
            }
            
            if (column.isPrimaryKey()) {
                if (this.primaryKeyColumn != null) {
                    throw new IllegalArgumentException("Multiple Primary key Columns exist for table " + this.name);
                }
                this.primaryKeyColumn = column;
            }
        
            this.columns.add(column);
        }
    }
    
    /**
     * @return The column that is the primary key
     */
    public Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }
    
    private static Class<? extends ObjectHandler> determineObjectHandler(Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return null;
        }

        TableHandler tableHandler = clazz.getAnnotation(TableHandler.class);
        
        if (tableHandler == null) {
            return determineObjectHandler(clazz.getSuperclass());
        }

        return tableHandler.value();
    }

    private static String determineTableName(Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return null;
        }

        TableName tableName = clazz.getAnnotation(TableName.class);
        if (tableName == null || tableName.value().isEmpty()) {
            return determineTableName(clazz.getSuperclass());
        }

        return tableName.value();
    }
    
    /**
     * @return The database that this table is registered under.
     */
    public SQLDatabase getDatabase() {
        return database;
    }
    
    /**
     * @return The object handler for parsing the objects. Can be null
     */
    public Class<? extends ObjectHandler> getHandler() {
        return handler;
    }
    
    /**
     * @return The table name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return The Java Class this table represents
     */
    public Class<?> getModelClass() {
        return modelClass;
    }
    
    /**
     * @return All of the registered columns
     */
    public Set<Column> getColumns() {
        return new HashSet<>(columns);
    }
    
    /**
     * @param column Adds a column to the table
     */
    public void addColumn(Column column) {
        this.columns.add(column);
    }
    
    /**
     * @return The generated sql creation statement
     */
    public String generateCreationStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ");
        sb.append(getName()).append("(");
        getColumns().forEach(column -> {
            sb.append("`").append(column.getName()).append("`").append(" ").append(column.getType());
            if (column.isPrimaryKey()) {
                sb.append(" primary key");
            }
            
            if (column.isAutoIncrement()) {
                sb.append(" auto_increment");
            }
            
            if (column.isNotNull()) {
                sb.append(" not null");
            }
            
            sb.append(", ");
        });
        
        sb.delete(sb.length() - 2, sb.length() - 1);
        sb.append(");");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Table table = (Table) o;
        return Objects.equals(name, table.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public int compareTo(Table o) {
        return this.name.compareTo(o.name);
    }
    
    /**
     * @param columnName The name of the column. Case-Insensitive
     * @return The registered column, or null if it does not exist.
     */
    public Column getColumn(String columnName) {
        for (Column column : this.columns) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }
        return null;
    }
    
    /**
     * @return The passed in Logger instance
     */
    public Logger getLogger() {
        return database.getLogger();
    }
}
