package com.thenexusreborn.nexuscore.util;

import com.stardevllc.starcore.utils.color.ColorUtils;

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
    
    // %v is the variable placeholder
    public String format(String message, Object... replacements) {
        StringBuilder sb = new StringBuilder(getPrefixColor() + ">> " + getBaseColor());
        char[] msgChars = message.toCharArray();
        int replacementIndex = 0;
        for (int i = 0; i < msgChars.length; i++) {
            if (msgChars[i] != '%') {
                sb.append(msgChars[i]);
                continue;
            }
            
            if (msgChars[i + 1] == 'v') {
                //Should be now %v
                sb.append(getVariableColor()).append(replacements[replacementIndex++]).append(getBaseColor());
                i++;
            }
        }

        return ColorUtils.color(sb.toString());
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
