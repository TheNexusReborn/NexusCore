package com.thenexusreborn.api.sql.objects.typehandlers;

import com.stardevllc.starlib.observable.ReadOnlyProperty;
import com.thenexusreborn.api.sql.objects.TypeHandler;

public class PropertyTypeHandler extends TypeHandler {
    public PropertyTypeHandler() {
        super(ReadOnlyProperty.class, "varchar(10000)", (column, object) -> ((ReadOnlyProperty<?>) object).getValue(), null);
    }
}
