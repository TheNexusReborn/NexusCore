package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.PlayerProxy;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpigotPlayerProxy implements PlayerProxy {
    
    private UUID uuid;
    
    public SpigotPlayerProxy(UUID uuid) {
        this.uuid = uuid;
    }
    
    @Override
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(MCUtils.color(message));
        }
    }
    
    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }
}
