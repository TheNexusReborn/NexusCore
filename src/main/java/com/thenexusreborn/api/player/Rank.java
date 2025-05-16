package com.thenexusreborn.api.player;

import java.util.Objects;

public enum Rank {
    NEXUS("&4", true), 
    CONSOLE("&4", false),
    ADMIN("&c", true),
    HEAD_MOD("&5", true, "HEAD MOD"),
    SR_MOD("&5", true, "SR MOD"),
    MOD("&5", true), 
    HELPER("&d", true), 
    MVP("&e&l", true), 
    VIP("&e", true),
    ARCHITECT("&a", true),
    MEDIA("&3", true), 
    PLATINUM("&b", true),
    DIAMOND("&b", true), 
    BRASS("&6", true),
    GOLD("&6", true), 
    INVAR("&7", true),
    IRON("&7", true), 
    MEMBER("&9", false, "");
    
    private final String color, prefixOverride;
    private final boolean bold;
    
    Rank(String color, boolean bold) {
        this(color, bold, null);
    }
    
    Rank(String color, boolean bold, String prefixOverride) {
        this.color = color;
        this.bold = bold;
        this.prefixOverride = prefixOverride;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getPrefixOverride() {
        return prefixOverride;
    }
    
    public String getPrefix() {
        String prefix = color;
        if (bold) {
            prefix += "&l";
        }
        prefix += Objects.requireNonNullElseGet(prefixOverride, this::name);
        return prefix;
    }
    
    public static Rank parseRank(String str) {
        try {
            return valueOf(str);
        } catch (Exception e) {
            if (str.equalsIgnoreCase("iron_pa")) {
                return Rank.INVAR;
            } else if (str.equalsIgnoreCase("gold_pa")) {
                return Rank.BRASS;
            } else if (str.equalsIgnoreCase("diamond_pa")) {
                return Rank.PLATINUM;
            }
        }
        
        return null;
    }

    public boolean isBold() {
        return bold;
    }
}
