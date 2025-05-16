package com.thenexusreborn.api.sql.objects.typehandlers;

import com.thenexusreborn.api.sql.objects.Column;
import com.thenexusreborn.api.sql.objects.TypeHandler;

public class FloatHandler extends TypeHandler {
    public FloatHandler() {
        super(Float.class, "float", FloatHandler::parse, FloatHandler::parse);
        addAdditionalClass(float.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.floatValue();
        } else if (object instanceof String str) {
            return Float.parseFloat(str);
        }
        return 0.0F;
    }
}
