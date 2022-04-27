package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.scoreboard.SpigotNexusScoreboard;
import com.thenexusreborn.nexuscore.scoreboard.impl.SpigotScoreboard;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SpigotPlayerManager extends PlayerManager implements Listener {
    
    private final NexusCore plugin;
    
    public SpigotPlayerManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String originalJoinMessage = e.getJoinMessage();
        e.setJoinMessage(null);
        ActionBar actionBar = new ActionBar("&cPlease wait while your data is loaded.");
        actionBar.send(e.getPlayer());
        BukkitRunnable abr = new BukkitRunnable() {
            @Override
            public void run() {
                actionBar.send(e.getPlayer());
            }
        };
        abr.runTaskTimer(plugin, 20L, 20L);
        if (!players.containsKey(e.getPlayer().getUniqueId())) {
            getNexusPlayerAsync(e.getPlayer().getUniqueId(), nexusPlayer -> {
                NexusScoreboard nexusScoreboard = new SpigotNexusScoreboard(nexusPlayer);
                nexusScoreboard.init();
                nexusPlayer.setScoreboard(nexusScoreboard);
                e.getPlayer().setScoreboard(((SpigotScoreboard) nexusScoreboard.getScoreboard()).getScoreboard());
                
                if (!players.containsKey(e.getPlayer().getUniqueId())) {
                    players.put(e.getPlayer().getUniqueId(), nexusPlayer);
                }
                
                NexusPlayerLoadEvent nexusPlayerLoadEvent = new NexusPlayerLoadEvent(nexusPlayer, originalJoinMessage);
                Bukkit.getPluginManager().callEvent(nexusPlayerLoadEvent);
                String joinMessage = nexusPlayerLoadEvent.getJoinMessage();
                if (joinMessage != null) {
                    Bukkit.broadcastMessage(MCUtils.color(joinMessage));
                }
                
                actionBar.setText("&aYour data has been loaded.");
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    nexusPlayer.sendMessage("&6&l>> &dWelcome to &5&lThe Nexus Reborn&5!");
                    nexusPlayer.sendMessage("&6&l>> &dThis server is a project to bring back TheNexusMC, as least, some of it.");
                    nexusPlayer.sendMessage("&6&l>> &dWe are currently in &aPre-Alpha &dso expect some bugs and instability, as well as a lack of features.");
                    nexusPlayer.sendMessage("&6&l>> &dIf you would like to support us, please go to &eshop.thenexusreborn.com &dThat would mean a lot to us.");
                    abr.cancel();
                }, 40L);
            });
        } else {
            NexusPlayer nexusPlayer = players.get(e.getPlayer().getUniqueId());
            NexusScoreboard nexusScoreboard = new SpigotNexusScoreboard(nexusPlayer);
            nexusScoreboard.init();
            nexusPlayer.setScoreboard(nexusScoreboard);
            e.getPlayer().setScoreboard(((SpigotScoreboard) nexusScoreboard.getScoreboard()).getScoreboard());
            actionBar.setText("&aYour data has been loaded.");
            Bukkit.getScheduler().runTaskLater(plugin, abr::cancel, 20L);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        NexusPlayer nexusPlayer = this.players.get(e.getPlayer().getUniqueId());
        if (nexusPlayer != null) {
            this.players.remove(e.getPlayer().getUniqueId()); //No need to save to database. And stat changes are saved automatically
        }
    }
    
    @Override
    public NexusPlayer createPlayerData(UUID uniqueId, String name) {
        throw new UnsupportedOperationException("Not allowed to create player data on a Spigot Server");
    }
}
