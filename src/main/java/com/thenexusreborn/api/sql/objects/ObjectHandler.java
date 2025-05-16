package com.thenexusreborn.api.sql.objects;

import com.thenexusreborn.api.sql.annotations.table.TableHandler;

/**
 * This class allows handling of an object during the different stages<br>
 * The idea behind this class was to allow loading of collections in a table as well as other complex types.<br>
 * These are slowly being implemented as time goes on, so this class will become unneeded in the future<br>
 * To use this class, simply create a child class and keep the same constructor structure. This is needed for the library<br>
 * Then just implement your logic into the methods as you see fit. Please see the method documentations for what they do.<br>
 * Then annotation your class using the {@link TableHandler} annotation and that's it
 */
public abstract class ObjectHandler {
    
    protected final Object object;
    protected final SQLDatabase database;
    protected final Table table;
    
    /**
     * Constructs an Object handler. This is called from the get methods in the {@link SQLDatabase} class
     * @param object The object instance.
     * @param database The database which the object came from
     * @param table The table which the object came from
     */
    public ObjectHandler(Object object, SQLDatabase database, Table table) {
        this.object = object;
        this.database = database;
        this.table = table;
    }
    
    /**
     * Things that happen after the object has been loaded from the database
     */
    public void afterLoad() {
        
    }
    
    /**
     * Things that happen before the object is saved to the database
     */
    public void beforeSave() {
        
    }
    
    /**
     * Things that happen after the object has been saved to the database
     */
    public void afterSave() {
        
    }
}