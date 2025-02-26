package com.thenexusreborn.nexuscore.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ServerReflection extends Reflection {
    
    public static final Class<?> SERVER_CLASS = getNMSClass("MinecraftServer");
    public static final Method GET_SERVER = getMethod(SERVER_CLASS, "getServer");
    public static final Field RECENT_TPS = getField(SERVER_CLASS, "recentTps");
    
    static {
        RECENT_TPS.setAccessible(true);
    }
    
    public double getRecentTps() {
        try {
            Object serverInstance = GET_SERVER.invoke(null);
            Object recentTps = RECENT_TPS.get(serverInstance);
            return Math.min(Math.round((double) Array.get(recentTps, 0) * 100.0) / 100.0, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
