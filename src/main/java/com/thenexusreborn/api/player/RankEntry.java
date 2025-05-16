package com.thenexusreborn.api.player;

import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("playerranks")
public class RankEntry {
    public enum Action {
        ADDED, REMOVED
    }
    
    @PrimaryKey 
    private long id;
    private UUID uniqueId;
    private Rank rank;
    private long timestamp;
    private long expire;
    private String actor;
    private Action action;
    
    public RankEntry(UUID uniqueId, Rank rank, long timestamp, long expire, String actor, Action action) {
        this.uniqueId = uniqueId;
        this.rank = rank;
        this.timestamp = timestamp;
        this.expire = expire;
        this.actor = actor;
        this.action = action;
    }
    
    public long getId() {
        return id;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public long getExpire() {
        return expire;
    }
    
    public String getActor() {
        return actor;
    }
    
    public Action getAction() {
        return action;
    }
}