package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public final class SpigotNexusPlayer extends NexusPlayer {
    public static final int version = 3;
    
    private ActionBar actionBar = new ActionBar();
    
    public SpigotNexusPlayer(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public SpigotNexusPlayer(UUID uniqueId, Map<Rank, Long> ranks, long firstJoined, long lastLogin, long lastLogout, long playTime, String lastKnownName, Tag tag) {
        super(uniqueId, ranks, firstJoined, lastLogin, lastLogout, playTime, lastKnownName, tag);
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
    public void sendMessage(String message) {
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(MCUtils.color(message));
        }
    }
}
