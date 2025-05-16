package com.thenexusreborn.api.sql.objects.typehandlers;

import com.thenexusreborn.api.sql.objects.Column;
import com.thenexusreborn.api.sql.objects.TypeHandler;

public class LongHandler extends TypeHandler {
    public LongHandler() {
        super(Long.class, "bigint", LongHandler::parse, LongHandler::parse);
        addAdditionalClass(long.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof Number number) {
            return number.longValue();
        } else if (object instanceof String str) {
            return Long.parseLong(str);
        }
        return 0;
    }
}
