package com.thenexusreborn.api.player;

import com.stardevllc.starlib.converter.string.EnumStringConverter;
import com.stardevllc.starlib.converter.string.StringConverters;

import java.util.List;
import java.util.Objects;

public enum Rank {
    NEXUS("&4", true, List.of(
            "vulcan.*",
            "luckperms.*",
            "starchat.clearchat.flags.*",
            "starchat.clearchat.bypass", 
            "starchat.privatemessage.visibility.bypass", 
            "staritems.admin", 
            "staritems.admin.*"
            ), List.of()
    ), 
    CONSOLE("&4", false),
    ADMIN("&c", true, List.of(
            "viaversion.admin"
            ), List.of()
    ),
    HEAD_MOD("&5", true, "HEAD MOD"),
    SR_MOD("&5", true, "SR MOD"),
    MOD("&5", true, List.of(
            "starchat.mutechat"
        ), List.of()
    ), 
    HELPER("&d", true, List.of(
            "nexuscore.punishments.notify", 
            "nexuscore.staff.send", 
            "nexuscore.staff.view", 
            "vulcan.alerts", 
            "vulcan.logs", 
            "starchat.clearchat"
            ), List.of()
    ), 
    MVP("&e", true, true), 
    VIP("&e", true),
    ARCHITECT("&a", true),
    MEDIA("&3", true), 
    PLATINUM("&b", true, true),
    DIAMOND("&b", true), 
    BRASS("&6", true, true),
    GOLD("&6", true), 
    INVAR("&7", true, true),
    IRON("&7", true), 
    MEMBER("&9", false, "", List.of(
            "starchat.command.chat", 
            "starchat.command.message", 
            "starchat.command.reply"), 
            List.of(
                    "bukkit.command.plugins")
    );
    
    static {
        StringConverters.addConverter(Rank.class, new EnumStringConverter<>(Rank.class));
    }
    
    private final String color, prefixOverride;
    private final boolean bold, nameBold;
    
    private final List<String> permissions;
    private final List<String> negatedPermissions;
    
    Rank(String color, boolean bold) {
        this(color, null, bold, false, List.of(), List.of());
    }
    
    Rank(String color, boolean bold, List<String> permissions, List<String> negatedPermissions) {
        this(color, null, bold, false, permissions, negatedPermissions);
    }
    
    Rank(String color, boolean bold, String prefixOverride) {
        this(color, prefixOverride, bold, false, List.of(), List.of());
    }
    
    Rank(String color, boolean bold, String prefixOverride, List<String> permissions, List<String> negatedPermissions) {
        this(color, prefixOverride, bold, false, permissions, negatedPermissions);
    }
    
    Rank(String color, boolean bold, boolean nameBold) {
        this(color, null, bold, nameBold, List.of(), List.of());
    }
    
    Rank(String color, boolean bold, boolean nameBold, List<String> permissions, List<String> negatedPermissions) {
        this(color, null, bold, nameBold, permissions, negatedPermissions);
    }
    
    Rank(String color, String prefixOverride, boolean bold, boolean nameBold, List<String> permissions, List<String> negatedPermissions) {
        this.color = color;
        this.prefixOverride = prefixOverride;
        this.bold = bold;
        this.nameBold = nameBold;
        this.permissions = permissions;
        this.negatedPermissions = negatedPermissions;
    }
    
    public String getColor() {
        return color;
    }
    
    public boolean isNameBold() {
        return nameBold;
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
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public List<String> getNegatedPermissions() {
        return negatedPermissions;
    }
}
