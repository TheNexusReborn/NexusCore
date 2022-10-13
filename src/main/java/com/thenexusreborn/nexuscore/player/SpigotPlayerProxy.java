package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.PlayerProxy;
import com.thenexusreborn.nexuscore.util.MCUtils;
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
            player.sendMessage(MCUtils.color(message));
        }
    }
    
    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(uniqueId) != null;
    }
    
    @Override
    public String getName() {
        return Bukkit.getPlayer(uniqueId).getName();
    }
}
