package com.thenexusreborn.nexuscore.player;

import com.stardevllc.starcore.utils.ProgressBar;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.experience.ExperienceLevel;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.api.player.NexusPlayer;

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
        if (System.currentTimeMillis() >= this.time + 5000) {
            player.setActionBar(previous);
            return "";
        }
        
        int level = player.getExperience().getLevel();
        int currentXp = (int) Math.round(player.getExperience().getLevelXp());
        int nextLevelXp;
        Map<Integer, ExperienceLevel> playerLevels = NexusReborn.getLevelManager().getPlayerLevels();
        if (playerLevels.containsKey(level + 1)) {
            nextLevelXp = playerLevels.get(level + 1).getXpRequired();
        } else {
            return "";
        }

        String bar = ProgressBar.of(currentXp, nextLevelXp, 70, "|", "&a", "&c");
        return "&aLVL " + level + " &8[" + bar + "&8] &7" + currentXp + " XP&8/&7" + nextLevelXp + " XP";
    }
}
