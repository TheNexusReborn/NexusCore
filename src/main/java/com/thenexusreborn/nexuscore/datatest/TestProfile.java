package com.thenexusreborn.nexuscore.datatest;

import com.thenexusreborn.api.data.annotations.*;
import com.thenexusreborn.api.data.codec.*;
import com.thenexusreborn.api.tags.Tag;

import java.util.UUID;

/**
 * This is a test class for the database stuff
 */
@TableInfo("profiles")
public class TestProfile {
    @Primary
    private long id;
    
    @ColumnInfo(type = "varchar(36)", notNull = true, codec = UUIDCodec.class)
    private UUID uniqueId;
    
    private String name;
    
    private int level;
    private long playTime;
    private double xp;
    
    private boolean online;
    
    @ColumnInfo(type = "varchar(100)", codec = TagCodec.class)
    private Tag tag;
    
    public TestProfile(UUID uniqueId, String name, int level, long playTime, double xp, boolean online, Tag tag) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.level = level;
        this.playTime = playTime;
        this.xp = xp;
        this.online = online;
        this.tag = tag;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public long getPlayTime() {
        return playTime;
    }
    
    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }
    
    public double getXp() {
        return xp;
    }
    
    public void setXp(double xp) {
        this.xp = xp;
    }
    
    public boolean isOnline() {
        return online;
    }
    
    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public Tag getTag() {
        return tag;
    }
    
    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
