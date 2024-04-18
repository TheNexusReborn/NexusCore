package com.thenexusreborn.nexuscore.player;

import com.stardevllc.starcore.color.ColorUtils;
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
            player.sendMessage(ColorUtils.color(message));
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
