package com.thenexusreborn.api.nickname.player;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.PlayerTime;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("nicktimes")
public class NickTime extends PlayerTime {
    
    private boolean persist;
    
    @ColumnIgnored
    private PlayerTime trueTime;
    
    protected NickTime() {
    }
    
    public NickTime(UUID uniqueId) {
        super(uniqueId);
    }
    
    public NickTime(UUID uniqueId, PlayerTime trueTime) {
        super(uniqueId);
        this.trueTime = trueTime;
    }
    
    public NickTime(UUID uniqueId, long playTime, PlayerTime trueTime) {
        super(uniqueId);
        this.playtime = playTime;
        this.firstJoined = System.currentTimeMillis() - playtime;
        this.trueTime = trueTime;
    }
    
    public void setPersist(boolean persist) {
        this.persist = persist;
    }
    
    public boolean isPersist() {
        return persist;
    }
    
    @Override
    public long addPlaytime(long playtime) {
        getTrueTime().addPlaytime(playtime);
        return super.addPlaytime(playtime);
    }
    
    public PlayerTime getTrueTime() {
        if (this.trueTime == null) {
            this.trueTime = NexusAPI.getApi().getPlayerManager().getNexusPlayer(this.getUniqueId()).getTrueTime();
        }
        
        return trueTime;
    }
    
    public void setTrueTime(PlayerTime trueTime) {
        this.trueTime = trueTime;
    }
}
