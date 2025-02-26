package com.thenexusreborn.nexuscore.reflection.impl;

import com.mojang.authlib.GameProfile;
import com.thenexusreborn.nexuscore.reflection.ProfileReflection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;

public class PlayerSkull extends ProfileReflection {
    
    public static final Class<?> CRAFT_SKULL = getCraftClass("inventory.CraftMetaSkull");
    public static final Field PROFILE_FIELD = getField(CRAFT_SKULL, "profile");
    
    static {
        PROFILE_FIELD.setAccessible(true);
    }
    
    public ItemStack getSkull(Player player) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        try {
            GameProfile profile = getProfile(player);
            PROFILE_FIELD.set(skullMeta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
