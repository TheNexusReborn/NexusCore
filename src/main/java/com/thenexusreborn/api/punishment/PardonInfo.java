package com.thenexusreborn.api.punishment;

import com.thenexusreborn.api.NexusAPI;

import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class PardonInfo {
    private final long date;
    private final String actor, reason;
    private String actorNameCache;
    
    public PardonInfo(long date, String actor, String reason) {
        this.date = date;
        this.actor = actor;
        this.reason = reason;
    }
    
    public long getDate() {
        return date;
    }
    
    public String getActor() {
        return actor;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "PardonInfo{" +
                "date=" + date +
                ", actor='" + actor + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
    
    public void setActorNameCache(String actorNameCache) {
        this.actorNameCache = actorNameCache;
    }
    
    public String getActorNameCache() {
        if (actorNameCache == null) {
            try {
                UUID uuid = UUID.fromString(getActor());
                actorNameCache = NexusAPI.getApi().getPlayerManager().getNameFromUUID(uuid);
                if (actorNameCache == null) {
                    actorNameCache = actor;
                } 
            } catch (Exception e) {
                this.actorNameCache = actor;
            }
        }
        
        return actorNameCache;
    }
}
