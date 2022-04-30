package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.api.util.Operator;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class SpigotNexusPlayer extends NexusPlayer {
    public static final int version = 3;
    
    private ActionBar actionBar = new ActionBar();
    
    public SpigotNexusPlayer(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public SpigotNexusPlayer(UUID uniqueId, Map<Rank, Long> ranks, long firstJoined, long lastLogin, long lastLogout, long playTime, String lastKnownName, Tag tag, Set<Tag> unlockedTags) {
        super(uniqueId, ranks, firstJoined, lastLogin, lastLogout, playTime, lastKnownName, tag, unlockedTags);
    }
    
    @Override
    public String getNameFromServer() {
        Player player = getPlayer();
        if (player != null) {
            return player.getName();
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.getUniqueId());
            if (offlinePlayer != null) {
                return offlinePlayer.getName();
            }
        }
        return null;
    }
    
    public ActionBar getActionBar() {
        return actionBar;
    }
    
    public void setActionBar(ActionBar actionBar) {
        if (actionBar == null) {
            this.actionBar = new ActionBar();
        } else {
            this.actionBar = actionBar;
        }
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }
    
    @Override
    public <T extends Number> void changeStat(String statName, T statValue, Operator operator) {
        super.changeStat(statName, statValue, operator);
        
        if (!statName.equalsIgnoreCase("xp")) {
            return;
        }
    
        int level = getLevel();
        int currentXp = (int) (getStatValue("xp") + getPlayTimeXp());
        int levelXp = currentXp - NexusPlayer.levels.get(level);
        int nextLevelXp = NexusPlayer.levels.get(level + 1) - NexusPlayer.levels.get(level);
        int xpToNextLevel = nextLevelXp - levelXp;
        ProgressBar progressBar = new ProgressBar(levelXp, nextLevelXp, 100, "|", "&a", "&c");
        
        String format = "&aLVL " + level + "&8[" + progressBar.display() + "&8] &7" + levelXp + " XP&8/&7" + nextLevelXp + "XP";
        setActionBar(new ActionBar(format));
        ActionBar current = getActionBar();
        new BukkitRunnable() {
            @Override
            public void run() {
                setActionBar(current);
            }
        }.runTaskLater(NexusCore.getPlugin(NexusCore.class), 60L);
    }
    
    @Override
    public void sendMessage(String message) {
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(MCUtils.color(message));
        }
    }
}
