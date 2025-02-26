package com.thenexusreborn.nexuscore.reflection;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
 
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameters) {
        try {
            return clazz.getDeclaredConstructor(parameters);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Object getFieldValue(Class<?> clazz, String name, Object object) {
        try {
            return getField(clazz, name).get(object);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameters) {
        try {
            return clazz.getDeclaredMethod(name, parameters);
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> getCraftClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + "." + name);
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server" + NMS_VERSION + "." + name);
        } catch (Exception ex) {
            return null;
        }
    }
}
