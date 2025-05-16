package com.thenexusreborn.api.sql.objects.typehandlers;

import com.thenexusreborn.api.sql.objects.Column;
import com.thenexusreborn.api.sql.objects.TypeHandler;

public class StringHandler extends TypeHandler {
    public StringHandler() {
        super(String.class, "varchar(1000)", StringHandler::parse, StringHandler::parse);
        addAdditionalClass(Character.class, char.class);
    }
    
    private static Object parse(Column column, Object object) {
        if (object instanceof String str) {
            return str;
        } else if (object instanceof Character character) {
            return character;
        } else if (object != null) {
            return object.toString();
        }
        return "";
    }
}
