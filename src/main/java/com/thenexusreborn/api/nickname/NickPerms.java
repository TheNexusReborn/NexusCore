package com.thenexusreborn.api.nickname;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

@TableName("nickperms")
public class NickPerms {
    @PrimaryKey
    private long pk = 1;
    
    private Rank customName = Rank.MEDIA;
    private Rank selfTarget = Rank.MEDIA;
    private Rank customRank = Rank.MEDIA;
    private Rank setOther = Rank.ADMIN;
    private Rank customLevel = Rank.VIP;
    private Rank customSkin = Rank.VIP;
    private Rank customCredits = Rank.VIP;
    private Rank customNexites = Rank.VIP;
    private Rank customTime = Rank.VIP;
    private Rank persistStats = Rank.VIP;
    
    public void setCustomName(Rank customName) {
        this.customName = customName;
    }
    
    public void setSelfTarget(Rank selfTarget) {
        this.selfTarget = selfTarget;
    }
    
    public void setCustomRank(Rank customRank) {
        this.customRank = customRank;
    }
    
    public void setSetOther(Rank setOther) {
        this.setOther = setOther;
    }
    
    public void setCustomLevel(Rank customLevel) {
        this.customLevel = customLevel;
    }
    
    public void setCustomSkin(Rank customSkin) {
        this.customSkin = customSkin;
    }
    
    public void setCustomCredits(Rank customCredits) {
        this.customCredits = customCredits;
    }
    
    public void setCustomNexites(Rank customNexites) {
        this.customNexites = customNexites;
    }
    
    public void setCustomTime(Rank customTime) {
        this.customTime = customTime;
    }
    
    public void setPersistStats(Rank persistStats) {
        this.persistStats = persistStats;
    }
    
    public Rank getCustomName() {
        return customName;
    }
    
    public Rank getSelfTarget() {
        return selfTarget;
    }
    
    public Rank getCustomRank() {
        return customRank;
    }
    
    public Rank getSetOther() {
        return setOther;
    }
    
    public Rank getCustomLevel() {
        return customLevel;
    }
    
    public Rank getCustomSkin() {
        return customSkin;
    }
    
    public Rank getCustomCredits() {
        return customCredits;
    }
    
    public Rank getCustomNexites() {
        return customNexites;
    }
    
    public Rank getCustomTime() {
        return customTime;
    }
    
    public Rank getPersistStats() {
        return persistStats;
    }
}