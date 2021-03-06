package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.api.util.Operator;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public final class SpigotNexusPlayer extends NexusPlayer {
    public static final int version = 3;
    
    private ActionBar actionBar = new ActionBar();
    private boolean spokenInChat = false;
    
    //TODO Make proxy methods for Player methods that has a Player null check to prevent issues and change all interactions with Bukkit Player to use these methods
    
    public SpigotNexusPlayer(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public SpigotNexusPlayer(UUID uniqueId, Map<Rank, Long> ranks, long firstJoined, long lastLogin, long lastLogout, long playTime, String lastKnownName, Tag tag, Set<Tag> unlockedTags, boolean prealpha, boolean alpha, boolean beta) {
        super(uniqueId, ranks, firstJoined, lastLogin, lastLogout, playTime, lastKnownName, tag, unlockedTags, prealpha, alpha, beta);
    }
    
    public void setSpokenInChat(boolean spokenInChat) {
        this.spokenInChat = spokenInChat;
    }
    
    public boolean hasSpokenInChat() {
        return this.spokenInChat;
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
         int previousLevel = 0;
         if (statName.equalsIgnoreCase("xp")) {
             previousLevel = getLevel();
         }
        super.changeStat(statName, statValue, operator);
        
        if (!statName.equalsIgnoreCase("xp")) {
            return;
        }
        
        if (getLevel() > previousLevel) {
            sendMessage("&5?????????????????????????????????????????????????????????????????????????????????????????????");
            sendMessage("&5???  &5&k&liiii&a&lLEVEL UP!&5&l&kiiii");
            sendMessage("&5???  &7&oYou are now &e&l&oLevel " + getLevel() + "&7&o!");
            sendMessage("&5?????????????????????????????????????????????????????????????????????????????????????????????");
        }
    
        if (getActionBar() instanceof XPActionBar) {
            XPActionBar actionBar = (XPActionBar) getActionBar();
            actionBar.update();
        } else {
            setActionBar(new XPActionBar(this, getActionBar(), System.currentTimeMillis()));
        }
    }
    
    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(this.getUniqueId()) != null;
    }
    
    @Override
    public void sendMessage(String message) {
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(MCUtils.color(message));
        }
    }
}
