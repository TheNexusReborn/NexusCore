package com.thenexusreborn.nexuscore.util;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * This class is meant to represent an ActionBar. You really only need to create one instance per actionbar and just use the send method
 */
public class ActionBar {
    
    private String text;
    
    public ActionBar() {
    }
    
    public ActionBar(String text) {
        this.text = MCUtils.color(text);
    }
    
    public void setText(String message) {
        this.text = MCUtils.color(message);
    }
    
    public String getText() {
        return text;
    }
    
    /**
     * Sends this ActionBar to a player
     * @param player The player to send it to.
     */
    public void send(Player player) {
        if (text != null) {
            PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(getText()), (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
