package com.thenexusreborn.nexuscore.util;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class SpigotUtils {
    public static void sendActionBar(Player player, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(MCUtils.color(text)), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
