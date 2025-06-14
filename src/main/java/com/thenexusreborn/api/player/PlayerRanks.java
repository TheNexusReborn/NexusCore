package com.thenexusreborn.api.player;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRanks {
    private UUID uniqueId;
    protected final Map<Rank, Long> ranks = new EnumMap<>(Rank.class);
    
    public PlayerRanks(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public Rank get() {
        if (this.uniqueId == null) {
            return Rank.MEMBER;
        }
        
        if (this.uniqueId.toString().equals("3f7891ce-5a73-4d52-a2ba-299839053fdc")) {
            this.ranks.put(Rank.NEXUS, -1L);
            return Rank.NEXUS;
        }
        
        if (this.ranks.isEmpty()) {
            this.ranks.put(Rank.MEMBER, -1L);
            return Rank.MEMBER;
        }
    
        for (Map.Entry<Rank, Long> entry : new EnumMap<>(ranks).entrySet()) {
            if (entry.getValue() == -1) {
                return entry.getKey();
            }
        
            if (System.currentTimeMillis() <= entry.getValue()) {
                return entry.getKey();
            }
        }
    
        return Rank.MEMBER;
    }
    
    public void add(Rank rank, long expire) {
        this.ranks.put(rank, expire);
    }
    
    public void set(Rank rank, long expire) {
        if (this.uniqueId.toString().equals("3f7891ce-5a73-4d52-a2ba-299839053fdc")) {
            return;
        }
        
        this.ranks.clear();
        this.ranks.put(rank, expire);
    }
    
    public void remove(Rank rank) {
        if (this.uniqueId.toString().equals("3f7891ce-5a73-4d52-a2ba-299839053fdc")) {
            return;
        }
        
        this.ranks.remove(rank);
    }
    
    public Map<Rank, Long> findAll() {
        EnumMap<Rank, Long> rankLongEnumMap = new EnumMap<>(this.ranks);
        rankLongEnumMap.put(get(), getExpire(get()));
        return rankLongEnumMap;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public boolean contains(Rank rank) {
        return this.ranks.containsKey(rank);
    }
    
    public long getExpire(Rank rank) {
        if (this.ranks.containsKey(rank)) {
            return this.ranks.get(rank);
        }
        
        return -1;
    }
    
    public void setAll(PlayerRanks ranks) {
        this.ranks.clear();
        this.ranks.putAll(ranks.findAll());
    }
}
