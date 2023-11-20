package com.thenexusreborn.nexuscore.util;

import org.bukkit.command.CommandSender;

public enum MsgType {
    
    // Codes to use in messages: &bc for base color and &vc for variable color
    
    INFO("&6", "&e", "&b"),
    SUCCESS("&6", "&a", "&b"),
    IMPORTANT("&2", "&a", "&b"),
    WARN("&6", "&c", "&e"),
    ERROR("&4", "&7", "&c"),
    SEVERE("&4", "&4", "&c"),
    DETAIL("&d", "&7", "&e"),
    VERBOSE("&6", "&7&o", "&e&o");
    
    private final String prefixColor, baseColor, variableColor;

    MsgType(String prefixColor, String baseColor, String variableColor) {
        this.prefixColor = prefixColor;
        this.baseColor = baseColor;
        this.variableColor = variableColor;
    }
    
    public String formatMsg(String message) {
        message  = "&bc" + message;
        return MCUtils.color(message.replaceAll("&bc", baseColor).replaceAll("&vc", variableColor));
    }
    
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(formatMsg(message));
    }
    
    public String getPrefixColor() {
        return prefixColor;
    }
    
    public String getBaseColor() {
        return baseColor;
    }
    
    public String getVariableColor() {
        return variableColor;
    }
    
    @Override
    public String toString(){
        return prefixColor + "&l>> " + baseColor;
    }
}
