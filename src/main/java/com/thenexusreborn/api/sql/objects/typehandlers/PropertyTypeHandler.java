package com.thenexusreborn.api.sql.objects.typehandlers;

import com.stardevllc.observable.Property;
import com.thenexusreborn.api.sql.objects.TypeHandler;

public class PropertyTypeHandler extends TypeHandler {
    public PropertyTypeHandler() {
        super(Property.class, "varchar(10000)", (column, object) -> ((Property<?>) object).getValue(), null);
    }
}
