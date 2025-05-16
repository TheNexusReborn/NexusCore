package com.thenexusreborn.api.player;

import java.lang.reflect.*;
import java.util.UUID;

public abstract class PlayerProxy {
    private static Class<? extends PlayerProxy> proxyClass;
    private static Constructor<? extends PlayerProxy> proxyConstructor;
    
    public static void setProxyClass(Class<? extends PlayerProxy> proxyClass) {
        PlayerProxy.proxyClass = proxyClass;
        try {
            proxyConstructor = proxyClass.getDeclaredConstructor(UUID.class);
        } catch (Exception e) {}
    }
    
    public static PlayerProxy of(UUID uniqueId) {
        if (proxyClass == null) {
            return null;
        }
        
        try {
            return proxyConstructor.newInstance(uniqueId);
        } catch (Exception e) {
            return null;
        }
    }
    
    protected final UUID uniqueId;
    
    public PlayerProxy(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public abstract void sendMessage(String message);
    
    public abstract boolean isOnline();
    
    public abstract String getName();
    
    public final UUID getUniqueId() {
        return uniqueId;
    }

    public void showXPActionBar() {
        
    }
}