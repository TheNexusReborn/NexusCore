package com.thenexusreborn.api.punishment;

import com.thenexusreborn.api.player.Rank;

public enum PunishmentType {
    BAN("&4", "banned", Rank.MOD, Rank.ADMIN), 
    MUTE("&9", "muted", Rank.HELPER, Rank.MOD), 
    KICK("&a", "kicked", Rank.HELPER, null), 
    WARN("&e", "warned", Rank.HELPER, null), 
    BLACKLIST("&8", "blacklisted", Rank.NEXUS, null);
    
    private final String color, verb;
    private final Rank minRankTemporary, minRankPermanent;
    
    PunishmentType(String color, String verb, Rank minRankTemporary, Rank minRankPermanent) {
        this.color = color;
        this.verb = verb;
        this.minRankTemporary = minRankTemporary;
        this.minRankPermanent = minRankPermanent;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getVerb() {
        return verb;
    }
    
    public Rank getMinRankTemporary() {
        return minRankTemporary;
    }
    
    public Rank getMinRankPermanent() {
        return minRankPermanent;
    }
}
