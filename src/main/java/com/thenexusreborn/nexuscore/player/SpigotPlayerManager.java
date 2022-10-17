package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.PlayerManager;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.task.PlayerJoinTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class SpigotPlayerManager extends PlayerManager implements Listener {
    
    private final NexusCore plugin;

    public SpigotPlayerManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            NexusPlayer player = getNexusPlayer(e.getPlayer().getUniqueId());
            if (player != null) {
                player.incrementCPS();
                if (player.getCPS() > 16) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();
        for (Player o : Bukkit.getOnlinePlayers()) {
            o.hidePlayer(player);

            NexusPlayer p = getNexusPlayer(o.getUniqueId());
            if (p == null) {
                player.hidePlayer(o);
            }
        }

        new PlayerJoinTask(plugin, player).start();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        NexusPlayer nexusPlayer = this.players.get(e.getPlayer().getUniqueId());
        this.handlePlayerLeave(nexusPlayer);
        this.players.remove(e.getPlayer().getUniqueId());
        e.setQuitMessage(null);
    }
    
    @Override
    public NexusPlayer createPlayerData(UUID uniqueId, String name) {
        throw new UnsupportedOperationException("Not allowed to create player data on a Spigot Server");
    }
    
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("minecraft:")) {
            if (!e.getPlayer().hasPermission("nexuscore.admin")) {
                e.setCancelled(true);
            }
        }
    }
}
