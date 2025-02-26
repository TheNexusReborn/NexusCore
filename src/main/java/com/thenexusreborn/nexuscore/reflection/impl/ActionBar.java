package com.thenexusreborn.nexuscore.reflection.impl;

import com.thenexusreborn.nexuscore.reflection.ChatReflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class ActionBar extends ChatReflection {

    public static final Class<?> CHAT_PACKET = getNMSClass("PacketPlayOutChat");
    public static final Constructor<?> PACKET_CONSTRUCTOR = getConstructor(CHAT_PACKET, CHAT_BASE_COMPONENT, byte.class);
    
    private String text;

    public ActionBar() {}
    
    public ActionBar(String text) {
        this.text = text;
    }

    public void send(Player player) {
        try {
            Object packet = PACKET_CONSTRUCTOR.newInstance(serialize(text), (byte) 2);
            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void send(Player player, String text) {
        try {
            Object packet = PACKET_CONSTRUCTOR.newInstance(serialize(text), (byte) 2);
            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
