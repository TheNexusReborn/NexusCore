package com.thenexusreborn.api.sql;

import com.stardevllc.starlib.registry.StringRegistry;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.api.sql.objects.Table;
import com.thenexusreborn.api.sql.objects.TypeHandler;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class is how {@link SQLDatabase}'s are managed and initialized within this library<br>
 * You can create databases using the Database Constructors or by using the factory style methods in this class.<br>
 * If you create databases through the constructors, you still have to register them here using the register(Database) methods<br>
 * Databases must already exist in the MySQL Database. I am working on reworking this to allow creating them in the library<br>
 * It does not matter if you call the {@code setup()} method before or after you register all databases. The setup method processes them all at once, whereas if you register a database after calling the setup method, it is handled on each registration<br>
 * This is also the first class where you can customize the {@link TypeHandler}'s. All TypeHandlers registered to this class will be shared across all databases registered here as well. These MUST be registered before calling the setup method or before the database you want to use it is registered.
 * @see SQLDatabase
 */
@SuppressWarnings("DanglingJavadoc")
public class DatabaseRegistry extends StringRegistry<SQLDatabase> {
    
    private boolean setup;
    private Logger logger;
    
    private Set<TypeHandler> typeHandlers = new HashSet<>();
    
    public DatabaseRegistry(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * This is the main setup method. Call this method when you want the library to generate the tables
     *
     * @throws SQLException The passed exception if one occurs
     */
    public void setup() throws SQLException {
        for (SQLDatabase database : getObjects().values()) {
            for (Table table : database.getTables()) {
                database.execute(table.generateCreationStatement());
            }
        }
        this.setup = true;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    /**
     * @return If the setup() method has been called and no exceptions happened
     */
    public boolean isSetup() {
        return setup;
    }
    
    /**
     * Registers a Database. <br>
     * If the setup flag is true, this will generate the tables from the database being registered. <br>
     * This does not pass any of the exceptions that can happen
     * @param object The object to register
     */
    public SQLDatabase register(SQLDatabase object) {
        super.register(object.getName(), object);
        if (!this.setup) {
            for (Table table : object.getTables()) {
                try {
                    object.execute(table.generateCreationStatement());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        object.setRegistry(this);
        return object;
    }
    
    /**
     * Registers a new database using the supplied methods
     *
     * @param type     This can only be mysql and h2.
     * @param name     The name of the database
     * @param host     The host of the database
     * @param user     The user to use for the connection
     * @param password The password to use for the connection
     * @param primary  This is mainly for another project, will probably remove this
     * @return The created Database
     */
//    public SQLDatabase register(String type, String name, String host, String user, String password, boolean primary) {
//        SQLDatabase database = new SQLDatabase(this.logger, type, name, host, user, password, primary);
//        register(database);
//        return database;
//    }
    
    /**
     * Registers a new database using the supplied methods
     *
     * @param name     The name of the database
     * @param host     The host of the database
     * @param user     The user to use for the connection
     * @param password The password to use for the connection
     * @return The created database
     */
//    public SQLDatabase register(String name, String host, String user, String password) {
//        SQLDatabase database = new SQLDatabase(this.logger, name, host, user, password);
//        register(database);
//        return database;
//    }
    
    /**
     * Registers multiple databases.
     * If the setup flag is true, this will generate the tables from the databases being registered.
     * This does not pass any of the exceptions that can happen
     *
     * @param objects The databases to register
     */
    public void registerAll(Collection<SQLDatabase> objects) {
        for (SQLDatabase database : objects) {
            register(database);
            for (Table table : database.getTables()) {
                try {
                    database.execute(table.generateCreationStatement());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * @return All type handles associated with this DatabaseRegistry
     */
    public Set<TypeHandler> getTypeHandlers() {
        return new HashSet<>(typeHandlers);
    }
    
    /**
     * Gets a database by a name. This is case-insensitive
     *
     * @param str The database name
     * @return The registered database or null if one does not exist
     */
    @Override
    public SQLDatabase get(String str) {
        for (SQLDatabase object : getObjects().values()) {
            if (object.getName().equalsIgnoreCase(str)) {
                return object;
            }
        }
        return null;
    }
    
    /**
     * Adds a TypeHandler for all databases in this Registry
     * @param handler The TypeHandler to register
     */
    public void addTypeHandler(TypeHandler handler) {
        this.typeHandlers.add(handler);
    }
}
