package com.thenexusreborn.api.player;

import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("playertimes")
public class PlayerTime {
    @PrimaryKey
    private UUID uniqueId;
    protected long firstJoined;
    protected long lastLogin, lastLogout;
    protected long playtime;
    
    protected PlayerTime() {}

    public PlayerTime(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public long getFirstJoined() {
        return firstJoined;
    }

    public void setFirstJoined(long firstJoined) {
        this.firstJoined = firstJoined;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(long lastLogout) {
        this.lastLogout = lastLogout;
    }

    public long getPlaytime() {
        return playtime;
    }
    
    public long addPlaytime(long playtime) {
        this.playtime += playtime;
        return this.playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
