package com.thenexusreborn.nexuscore.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;

public final class SpigotUtils {
    public static void sendActionBar(Player player, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(MCUtils.color(text)), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
    
    public static ItemStack getPlayerSkull(Player player) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = ((SkullMeta) skull.getItemMeta());
        String playerName = Bukkit.getPlayer(player.getUniqueId()).getName();
        GameProfile mcProfile = ((CraftPlayer) player).getProfile();
        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, mcProfile);
        } catch (Exception e) {}
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
