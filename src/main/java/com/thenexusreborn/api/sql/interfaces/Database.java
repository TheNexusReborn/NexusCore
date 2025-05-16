package com.thenexusreborn.api.sql.interfaces;

import com.thenexusreborn.api.sql.DatabaseRegistry;

import java.util.List;
import java.util.logging.Logger;

public interface Database {
    //Credentials
    String getName();
    String getUser();
    String getPassword();
    String getUrl();
    
    //Other needed things
    void registerClass(Class<?> clazz);
    Logger getLogger();
    
    //Database registry
    DatabaseRegistry getRegistry();
    void setRegistry(DatabaseRegistry registry);
    
    //Getters
    <T> List<T> get(Class<T> clazz, String[] columns, Object[] values) throws Exception;
    <T> List<T> get(Class<T> clazz, String columnName, Object value) throws Exception;
    <T> List<T> get(Class<T> clazz) throws Exception;
    
    //Saving
    void saveSilent(Object object);
    void save(Object object) throws Exception;
    
    //Deleting
    int deleteSilent(Class<?> clazz, Object id);
    int deleteSilent(Object object);
    int deleteSilent(Class<?> clazz, Object id, Object[] columns, Object[] values);
    int delete(Object object) throws Exception;
    int delete(Class<?> clazz, Object id) throws Exception;
    int delete(Class<?> clazz, Object id, Object[] columns, Object[] values) throws Exception;
    
    //Queue Related
    void queue(Object object);
    void flush();
}
