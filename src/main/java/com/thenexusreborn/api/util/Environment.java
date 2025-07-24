package com.thenexusreborn.api.util;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

public enum Environment {
    DEVELOPMENT, EXPERIMENTAL, PRODUCTION;
    
    static {
        StringConverters.addConverter(Environment.class, new EnumStringConverter<>(Environment.class));
    }
}