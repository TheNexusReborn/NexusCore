package com.thenexusreborn.api.sql.objects.typehandlers;

import com.thenexusreborn.api.sql.objects.Column;
import com.thenexusreborn.api.sql.objects.TypeHandler;

public class DoubleHandler extends TypeHandler {
    public DoubleHandler() {
        super(Double.class, "double", DoubleHandler::parse, DoubleHandler::parse);
        addAdditionalClass(double.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.doubleValue();
        } else if (object instanceof String str) {
            return Double.parseDouble(str);
        }
        return 0.0;
    }
}
