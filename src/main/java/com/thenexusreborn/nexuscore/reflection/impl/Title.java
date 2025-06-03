package com.thenexusreborn.nexuscore.reflection.impl;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.nexuscore.reflection.ChatReflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Title extends ChatReflection {
    
    public static final Class<?> TITLE_PACKET = getNMSClass("PacketPlayOutTitle");
    public static final Class<?> TITLE_ACTION = TITLE_PACKET.getDeclaredClasses()[0];
    public static final Constructor<?> TITLE_CONSTRUCTOR = getConstructor(TITLE_ACTION, CHAT_BASE_COMPONENT, int.class, int.class, int.class);
    public static final Object TITLE_TYPE = getFieldValue(TITLE_ACTION, "TITLE", null);
    public static final Object SUBTITLE_TYPE = getFieldValue(TITLE_ACTION, "SUBTITLE", null);
    
    public static final Method sendTitleMethod = getMethod(Player.class, "sendTitle", String.class, String.class, int.class, int.class, int.class);
    
    protected String title, subtitle;
    protected int fadeInTime, showTime, fadeOutTime;

    public Title(String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeInTime = fadeInTime;
        this.showTime = showTime;
        this.fadeOutTime = fadeOutTime;
    }

    public void send(Player player) {
        if (sendTitleMethod != null) {
            try {
                sendTitleMethod.invoke(player, StarColors.color(title), StarColors.color(subtitle), fadeInTime, showTime, fadeOutTime);
                return;
            } catch (Exception e) {}
        }
        try {
            Object chatTitle = serialize(title);
            Object packet = TITLE_CONSTRUCTOR.newInstance(TITLE_TYPE, chatTitle, fadeInTime, showTime, fadeOutTime);

            Object chatsTitle = serialize(subtitle);
            Object spacket = TITLE_CONSTRUCTOR.newInstance(SUBTITLE_TYPE, chatsTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
            sendPacket(player, spacket);
        } catch (Exception ex) {
        }
    }
}
