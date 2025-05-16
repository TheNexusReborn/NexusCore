package com.thenexusreborn.api.experience;

import java.util.HashMap;
import java.util.Map;

public class LevelManager {
    private Map<Integer, ExperienceLevel> playerLevels = new HashMap<>();

    public void init() {
        addLevel(new ExperienceLevel(0, 0));
        for (int i = 1; i <= 1000; i++) {
            ExperienceLevel playerLevel = new ExperienceLevel(i, i * 1000);
            playerLevel.addReward(new CreditReward(i * 100));
            addLevel(playerLevel);
        }
    }
    
    public ExperienceLevel getLevel(int level) {
        return this.playerLevels.get(level);
    }

    public void addLevel(ExperienceLevel playerLevel) {
        this.playerLevels.put(playerLevel.getNumber(), playerLevel);
    }

    public Map<Integer, ExperienceLevel> getPlayerLevels() {
        return new HashMap<>(playerLevels);
    }
}
