package com.thenexusreborn.api.server;

import com.stardevllc.converter.string.EnumStringConverter;
import com.stardevllc.converter.string.StringConverters;

public enum ServerType {
    PROXY, //Bungee Proxy, this is handled a bit differently 
    INSTANCE, //This means that the server is a physical instance
    VIRTUAL; //This means that it is within an instance server. Used with SG multi-games
    
    static {
        StringConverters.addConverter(ServerType.class, new EnumStringConverter<>(ServerType.class));
    }
}
