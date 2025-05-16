package com.thenexusreborn.api.sql.objects.typehandlers;

import com.thenexusreborn.api.sql.objects.TypeHandler;

public class BooleanHandler extends TypeHandler {
    public BooleanHandler() {
        super(Boolean.class, "varchar(5)", (column, object) -> {
            if (object instanceof Number number) {
                return number.intValue() == 1 ? "true" : "false";
            } else if (object instanceof Boolean bool) {
                return bool ? "true" : "false";
            }
            return false;
        }, (column, object) -> {
            if (object instanceof Boolean bool) {
                return bool;
            } else if (object instanceof Number number) {
                return number.intValue() == 1;
            } else if (object instanceof String str) {
                return Boolean.parseBoolean(str);
            }
            return false;
        });
        addAdditionalClass(boolean.class);
    }
}
