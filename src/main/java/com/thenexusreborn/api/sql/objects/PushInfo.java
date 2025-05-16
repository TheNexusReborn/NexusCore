package com.thenexusreborn.api.sql.objects;

public class PushInfo {
    private final String sql;
    private final boolean generateKeys;
    private final Table table;
    private final ObjectHandler objectHandler;
    
    public PushInfo(String sql, boolean generateKeys, Table table, ObjectHandler handler) {
        this.sql = sql;
        this.generateKeys = generateKeys;
        this.table = table;
        this.objectHandler = handler;
    }
    
    public String getSql() {
        return sql;
    }
    
    public boolean isGenerateKeys() {
        return generateKeys;
    }
    
    public Table getTable() {
        return table;
    }
    
    public ObjectHandler getObjectHandler() {
        return objectHandler;
    }
}
