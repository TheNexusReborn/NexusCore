package com.thenexusreborn.nexuscore.reflection;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PacketReflection extends Reflection {

    public static final Class<?> CRAFTPLAYER = getCraftClass("entity.CraftPlayer");
    public static final Class<?> ENTITYPLAYER = getNMSClass("EntityPlayer");
    public static final Class<?> PACKET_CLASS = getNMSClass("Packet");
    public static final Class<?> PLAYER_CONNECTION_CLASS = getNMSClass("PlayerConnection");
    public static final Method PLAYER_GETHANDLE = getMethod(CRAFTPLAYER, "getHandle");
    public static final Method SEND_PACKET = getMethod(PLAYER_CONNECTION_CLASS, "sendPacket", PACKET_CLASS);
    public static final Field PLAYER_CONNECTION_FIELD = getField(ENTITYPLAYER, "playerConnection");

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = PLAYER_GETHANDLE.invoke(player);
            Object playerConnection = PLAYER_CONNECTION_FIELD.get(handle);
            SEND_PACKET.invoke(playerConnection, packet);
        } catch (Exception ex) {}
    }
}
