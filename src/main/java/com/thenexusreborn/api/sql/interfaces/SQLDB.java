package com.thenexusreborn.api.sql.interfaces;

import com.thenexusreborn.api.sql.objects.Row;
import com.thenexusreborn.api.sql.objects.Table;
import com.thenexusreborn.api.sql.objects.TypeHandler;

import java.util.List;
import java.util.Set;

public interface SQLDB extends Database {
    //SQL Query Methods
    void execute(String sql) throws Exception;
    void executePrepared(String sql, Object... args) throws Exception;
    List<Row> executeQuery(String sql) throws Exception;
    List<Row> executePreparedQuery(String sql, Object... args) throws Exception;

    //SQL Type Handlers - The type handlers will be moved to the Database interface when it is abstracted away from SQL
    Set<TypeHandler> getTypeHandlers();
    void addTypeHandler(TypeHandler handler);

    //SQL Table methods
    Set<Table> getTables();
    Table getTable(String name);
    Table getTable(Class<?> clazz);
}
