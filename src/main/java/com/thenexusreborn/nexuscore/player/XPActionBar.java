package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.experience.ExperienceLevel;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.util.ProgressBar;

import java.util.Map;

public class XPActionBar implements IActionBar {
    private final NexusPlayer player;
    private final IActionBar previous;
    private long time;
    
    public XPActionBar(NexusPlayer player, IActionBar previous, long time) {
        this.player = player;
        this.previous = previous;
        this.time = time;
    }
    
    public void update() {
        this.time = System.currentTimeMillis();
    }
    
    @Override
    public String getText() {
        if (System.currentTimeMillis() >= this.time + 3000) {
            player.setActionBar(previous);
            return "";
        }
        
        int level = player.getStatValue("level").getAsInt();
        int currentXp = (int) Math.round(player.getStatValue("xp").getAsDouble());
        int nextLevelXp;
        Map<Integer, ExperienceLevel> playerLevels = NexusAPI.getApi().getLevelManager().getPlayerLevels();
        if (playerLevels.containsKey(level + 1)) {
            nextLevelXp = playerLevels.get(level + 1).getXpRequired();
        } else {
            return "";
        }
        
        ProgressBar progressBar = new ProgressBar(currentXp, nextLevelXp, 100, "|", "&a", "&c");
        return "&aLVL " + level + "&8[" + progressBar.display() + "&8] &7" + currentXp + " XP&8/&7" + nextLevelXp + " XP";
    }
}
