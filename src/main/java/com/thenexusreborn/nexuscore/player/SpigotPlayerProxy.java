package com.thenexusreborn.nexuscore.player;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.PlayerProxy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpigotPlayerProxy extends PlayerProxy {
    
    public SpigotPlayerProxy(UUID uuid) {
        super(uuid);
    }
    
    @Override
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null) {
            player.sendMessage(StarColors.color(message));
        }
    }
    
    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(uniqueId) != null;
    }
    
    @Override
    public String getName() {
        Player player = Bukkit.getPlayer(uniqueId);
        
        if (player == null) {
            return null;
        }
        
        return player.getName();
    }

    @Override
    public void showXPActionBar() {
        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(this.uniqueId);
        if (nexusPlayer != null) {
            nexusPlayer.setActionBar(new XPActionBar(nexusPlayer, nexusPlayer.getActionBar(), 5000));
        }
    }
}
