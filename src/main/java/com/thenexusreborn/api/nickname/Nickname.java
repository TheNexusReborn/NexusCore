package com.thenexusreborn.api.nickname;

import com.thenexusreborn.api.nickname.player.*;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("nicknames")
public class Nickname {
    @PrimaryKey
    private UUID uniqueId; //uuid of the player of the nickname
    private String name; //The name of the nickname
    private String trueName; //The true name of the player
    private String skin; //The identifier of the Skin. This must be compatible with the StarCore SkinManager
    private Rank rank; //The rank displayed
    private boolean active;
    
    private boolean persist = false;
    
    @ColumnIgnored
    private NickExperience fakeExperience;
    
    @ColumnIgnored
    private NickBalance fakeBalance;
    
    @ColumnIgnored
    private NickTime fakeTime;
    
    private Nickname() {}
    
    public Nickname(UUID uniqueId, String name, String trueName, String skin, Rank rank) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.trueName = trueName;
        this.skin = skin;
        this.rank = rank;
        this.fakeExperience = new NickExperience(uniqueId);
        this.fakeBalance = new NickBalance(uniqueId);
        this.fakeTime = new NickTime(uniqueId);
    }
    
    public void copyFrom(Nickname nickname) {
        this.name = nickname.getName();
        this.skin = nickname.getSkin();
        this.rank = nickname.getRank();
        this.fakeExperience = nickname.getFakeExperience();
        this.fakeBalance = nickname.getFakeBalance();
        this.fakeTime = nickname.getFakeTime();
        this.persist = nickname.isPersist();
        this.active = nickname.isActive();
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSkin(String skin) {
        this.skin = skin;
    }
    
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    
    public boolean isPersist() {
        return persist;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        
        if (!active && !persist) {
            this.fakeTime = new NickTime(uniqueId, this.fakeTime.getTrueTime());
            this.fakeBalance = new NickBalance(uniqueId, this.fakeBalance.getTrueBalance());
            this.fakeExperience = new NickExperience(uniqueId, this.fakeExperience.getTrueExperience());
        }
    }
    
    public void setPersist(boolean persist) {
        this.persist = persist;
        this.fakeExperience.setPersist(persist);
        this.fakeBalance.setPersist(persist);
        this.fakeTime.setPersist(persist);
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTrueName() {
        return trueName;
    }
    
    public String getSkin() {
        return skin;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public NickExperience getFakeExperience() {
        return fakeExperience;
    }
    
    public NickBalance getFakeBalance() {
        return fakeBalance;
    }
    
    public NickTime getFakeTime() {
        return fakeTime;
    }
    
    public void setFakeExperience(NickExperience fakeExperience) {
        this.fakeExperience = fakeExperience;
        this.fakeExperience.setPersist(persist);
    }
    
    public void setFakeBalance(NickBalance fakeBalance) {
        this.fakeBalance = fakeBalance;
        this.fakeBalance.setPersist(persist);
    }
    
    public void setFakeTime(NickTime fakeTime) {
        this.fakeTime = fakeTime;
        this.fakeTime.setPersist(persist);
    }
}