package com.thenexusreborn.nexuscore.reflection;

import com.stardevllc.starcore.StarColors;

import java.lang.reflect.Method;

public class ChatReflection extends PacketReflection {
    
    public static final Class<?> CHAT_BASE_COMPONENT = getNMSClass("IChatBaseComponent");
    public static final Class<?> CHAT_SERIALIZER = CHAT_BASE_COMPONENT.getDeclaredClasses()[0];
    public static final Method SERIALIZER_A = getMethod(CHAT_SERIALIZER, "a", String.class);
    
    public ChatReflection() {
        
    }

    public Object serialize(String text) {
        try {
            return SERIALIZER_A.invoke(null, "{\"text\": \"" + StarColors.color(text) + "\"}");
        } catch (Exception ex) {
        }
        
        return null;
    }
}
