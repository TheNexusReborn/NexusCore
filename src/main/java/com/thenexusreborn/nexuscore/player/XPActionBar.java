package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.*;

public class XPActionBar extends ActionBar {
    private SpigotNexusPlayer player;
    private ActionBar previous;
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
        int currentXp = (int) (player.getStatValue("xp") + player.getPlayTimeXp());
        int levelXp = currentXp - NexusPlayer.levels.get(level);
        int nextLevelXp = NexusPlayer.levels.get(level + 1) - NexusPlayer.levels.get(level);
        int xpToNextLevel = nextLevelXp - levelXp;
        ProgressBar progressBar = new ProgressBar(levelXp, nextLevelXp, 100, "|", "&a", "&c");
        return "&aLVL " + level + "&8[" + progressBar.display() + "&8] &7" + levelXp + " XP&8/&7" + nextLevelXp + "XP";
    }
}
