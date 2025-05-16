package com.thenexusreborn.api.util;

import com.stardevllc.helper.ReflectionHelper;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getValue(Object object, String path) {
        if (!path.contains(".")) {
            return getFieldValue(object, path);
        }
        
        String[] split = path.split("\\.");
        
        Object newObject = getFieldValue(object, split[0]);
        String newPath = path.substring(split[0].length() + 1);
        
        return getValue(newObject, newPath);
    }
    
    public static Object getFieldValue(Object object, String fieldName) {
        Field field = ReflectionHelper.getClassField(object.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }
}
