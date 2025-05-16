package com.thenexusreborn.api.sql.objects.typehandlers;

import com.stardevllc.helper.ReflectionHelper;
import com.thenexusreborn.api.sql.objects.TypeHandler;

import java.lang.reflect.Method;

public class EnumHandler extends TypeHandler {
    public EnumHandler() {
        super(Enum.class, "varchar(1000)", (column, object) -> {
            if (object instanceof Enum<?> e) {
                return e.toString();
            }
            return null;
        }, (column, object) -> {
            if (object instanceof String str) {
                try {
                    Method valueOfMethod = ReflectionHelper.getClassMethod(column.getField().getType(), "valueOf", String.class);
                    valueOfMethod.setAccessible(true);
                    return valueOfMethod.invoke(null, str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return null;
        });
    }
    
    @Override
    public boolean matches(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return Enum.class.isAssignableFrom(clazz);
    }
}
