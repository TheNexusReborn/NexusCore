package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.api.player.IActionBar;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Deprecated
public class ActionBar implements IActionBar {
    
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
        String text = getText();
        if (text != null) {
            PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(MCUtils.color(text)), (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
