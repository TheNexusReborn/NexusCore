package com.thenexusreborn.api.util;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum Environment {
    DEVELOPMENT, EXPERIMENTAL, PRODUCTION;
    
    static {
        StringConverters.addConverter(Environment.class, new EnumStringConverter<>(Environment.class));
    }
}