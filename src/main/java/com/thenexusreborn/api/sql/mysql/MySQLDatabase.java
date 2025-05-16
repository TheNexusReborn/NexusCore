package com.thenexusreborn.api.sql.mysql;

import com.thenexusreborn.api.sql.objects.SQLDatabase;

import java.util.logging.Logger;

public class MySQLDatabase extends SQLDatabase {
    public MySQLDatabase(Logger logger, MySQLProperties properties) {
        super(logger, properties);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        this.url = "jdbc:mysql://" + properties.getHost();
        
        if (properties.getPort() != 0) {
            this.url += ":" + properties.getPort();
        }
        
        this.url += "/" + properties.getDatabaseName();
    }
}