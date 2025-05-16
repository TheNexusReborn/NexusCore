package com.thenexusreborn.api.sql.objects.typehandlers;

import com.thenexusreborn.api.sql.objects.Column;
import com.thenexusreborn.api.sql.objects.TypeHandler;

public class IntegerHandler extends TypeHandler {
    public IntegerHandler() {
        super(Integer.class, "int", IntegerHandler::parse, IntegerHandler::parse);
        addAdditionalClass(int.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.intValue();
        } else if (object instanceof String str) {
            return Integer.parseInt(str);
        }
        return 0;
    }
}
