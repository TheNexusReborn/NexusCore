package com.thenexusreborn.api.sql.objects;

public class SQLProperties {
    protected String databaseName, username, password;
    
    public SQLProperties setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }
    
    public SQLProperties setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public SQLProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
