package com.thenexusreborn.api.util;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum NetworkType {
    SINGLE, MULTI;
    
    static {
        StringConverters.addConverter(NetworkType.class, new EnumStringConverter<>(NetworkType.class));
    }
}
