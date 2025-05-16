package com.thenexusreborn.api.nickname.player;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.experience.PlayerExperience;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("nickexperience")
public class NickExperience extends PlayerExperience {
    
    private boolean persist;
    
    @ColumnIgnored
    private PlayerExperience trueExperience;
    
    public NickExperience(UUID uniqueId) {
        super(uniqueId);
    }
    
    public NickExperience(UUID uniqueId, PlayerExperience trueExperience) {
        super(uniqueId);
        this.trueExperience = trueExperience;
    }
    
    public NickExperience(UUID uniqueId, int level, PlayerExperience trueExperience) {
        super(uniqueId);
        this.level = level;
        this.trueExperience = trueExperience;
    }
    
    public boolean isPersist() {
        return persist;
    }
    
    public void setPersist(boolean persist) {
        this.persist = persist;
    }
    
    @Override
    public boolean addExperience(double xp) {
        super.addExperience(xp);
        return getTrueExperience().addExperience(xp);
    }
    
    public PlayerExperience getTrueExperience() {
        if (this.trueExperience == null) {
            this.trueExperience = NexusAPI.getApi().getPlayerManager().getNexusPlayer(this.uniqueId).getTrueExperience();
        }
        
        return trueExperience;
    }
    
    protected NickExperience() {}
    
    public void setTrueExperience(PlayerExperience trueExperience) {
        this.trueExperience = trueExperience;
    }
}
