package com.thenexusreborn.api.punishment;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

public enum Visibility {
    NORMAL, SILENT;
    
    static {
        StringConverters.addConverter(Visibility.class, new EnumStringConverter<>(Visibility.class));
    }
}