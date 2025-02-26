package com.thenexusreborn.nexuscore.reflection;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class ProfileReflection extends Reflection {
    
    public static final Class<?> CRAFT_PLAYER = getCraftClass("entity.CraftPlayer");
    public static final Method GET_PROFILE = getMethod(CRAFT_PLAYER, "getProfile");
    
    public GameProfile getProfile(Player player) {
        try {
            return (GameProfile) GET_PROFILE.invoke(player);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
