package com.thenexusreborn.nexuscore.util;

import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class SpigotUtils {
    
    private static Class<?> craftPlayerClass;
    private static Class<?> packetPlayOutChatClass;
    private static Class<?> packetClass;
    private static Class<?> chatSerializerClass;
    private static Method aMethod;
    private static Class<?> iChatBaseComponentClass;
    private static Method craftPlayerHandleMethod;
    private static Class<?> entityPlayerClass;
    private static Field playerConnectionField;
    private static Class<?> playerConnectionClass;
    private static Method sendPacketMethod;
    
    static {
        try {
            craftPlayerClass = Class.forName("org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer");
            packetPlayOutChatClass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutChat");
            packetClass = Class.forName("net.minecraft.server.v1_8_R3.Packet");
            chatSerializerClass = Class.forName("net.minecraft.server.v1_8_R3.IChatBaseComponent$ChatSerializer");
            aMethod = chatSerializerClass.getDeclaredMethod("a", String.class);
            iChatBaseComponentClass = Class.forName("net.minecraft.server.v1_8_R3.IChatBaseComponent");
            craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            entityPlayerClass = Class.forName("net.minecraft.server.v1_8_R3.EntityPlayer");
            playerConnectionField = entityPlayerClass.getDeclaredField("playerConnection");
            playerConnectionClass = Class.forName("net.minecraft.server.v1_8_R3.PlayerConnection");
            sendPacketMethod = playerConnectionClass.getDeclaredMethod("sendPacket", packetClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendActionBar(Player player, String text) {
        try {
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Object cbc = iChatBaseComponentClass.cast(aMethod.invoke(chatSerializerClass, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"));
            packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static ItemStack getPlayerSkull(Player player) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile mcProfile = ((CraftPlayer) player).getProfile();
        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, mcProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
