package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.levels.LevelManager;
import com.thenexusreborn.nexuscore.util.*;

public class XPActionBar extends ActionBar {
    private final SpigotNexusPlayer player;
    private final ActionBar previous;
    private long time;
    
    public XPActionBar(SpigotNexusPlayer player, ActionBar previous, long time) {
        this.player = player;
        this.previous = previous;
        this.time = time;
    }
    
    public void update() {
        this.time = System.currentTimeMillis();
    }
    
    @Override
    public String getText() {
        if (System.currentTimeMillis() >= (this.time + 3000)) {
            player.setActionBar(previous);
            return "";
        }
        
        int level = player.getLevel();
        int currentXp = (int) player.getStatValue("xp");
        int levelXp = currentXp - LevelManager.levels.get(level);
        int nextLevelXp = LevelManager.levels.get(level + 1) - LevelManager.levels.get(level);
        int xpToNextLevel = nextLevelXp - levelXp;
        ProgressBar progressBar = new ProgressBar(levelXp, nextLevelXp, 100, "|", "&a", "&c");
        return "&aLVL " + level + "&8[" + progressBar.display() + "&8] &7" + levelXp + " XP&8/&7" + nextLevelXp + "XP";
    }
}
