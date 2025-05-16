package com.thenexusreborn.api.experience;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("experience")
public class PlayerExperience {
    @PrimaryKey
    protected UUID uniqueId;
    protected int level;
    protected double levelXp;

    protected PlayerExperience() {}

    public PlayerExperience(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public boolean addExperience(double xp) {
        double currentXp = this.levelXp;
        double newXp = currentXp + xp;
        int currentLevel = this.level;
        LevelManager levelManager = NexusAPI.getApi().getLevelManager();
        ExperienceLevel nextLevel = levelManager.getLevel(currentLevel + 1);
        if (nextLevel == null) {
            this.levelXp = newXp;
            return false;
        }

        if (newXp >= nextLevel.getXpRequired()) {
            double leftOverXp = newXp - nextLevel.getXpRequired();
            this.level++;
            this.levelXp = leftOverXp;
            return true;
        } else {
            this.levelXp = newXp;
        }

        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(this.uniqueId);
        if (nexusPlayer != null) {
            nexusPlayer.showXPActionBar();
        }
        
        return false;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setLevelXp(double levelXp) {
        this.levelXp = levelXp;
    }

    public int getLevel() {
        return level;
    }

    public double getLevelXp() {
        return levelXp;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}