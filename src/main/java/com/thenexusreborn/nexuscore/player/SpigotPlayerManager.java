package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.StaffChat;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.scoreboard.SpigotNexusScoreboard;
import com.thenexusreborn.nexuscore.scoreboard.impl.*;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SpigotPlayerManager extends PlayerManager implements Listener {
    
    private final NexusCore plugin;
    
    public SpigotPlayerManager(NexusCore plugin) {
        this.plugin = plugin;
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : new HashSet<>(players.keySet())) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        players.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 12000);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String originalJoinMessage = e.getJoinMessage();
        e.setJoinMessage(null);
        if (!players.containsKey(e.getPlayer().getUniqueId())) {
            ActionBar actionBar = new ActionBar("&cPlease wait while your data is loaded.");
            actionBar.send(e.getPlayer());
            BukkitRunnable abr = new BukkitRunnable() {
                @Override
                public void run() {
                    actionBar.send(e.getPlayer());
                }
            };
            abr.runTaskTimer(plugin, 20L, 20L);
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
                
                if (nexusScoreboard.getTablistHandler() == null) {
                    nexusScoreboard.setTablistHandler(new RankTablistHandler(nexusScoreboard));
                }
                
                actionBar.setText("&aYour data has been loaded.");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        abr.cancel();
                    }
                }.runTaskLater(plugin, 40L);
                
                if (nexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
                    StaffChat.sendJoin(nexusPlayer);
                }
            });
        } else {
            NexusPlayer nexusPlayer = players.get(e.getPlayer().getUniqueId());
            NexusScoreboard nexusScoreboard = new SpigotNexusScoreboard(nexusPlayer);
            nexusScoreboard.init();
            nexusPlayer.setScoreboard(nexusScoreboard);
            e.getPlayer().setScoreboard(((SpigotScoreboard) nexusScoreboard.getScoreboard()).getScoreboard());
            NexusPlayerLoadEvent nexusPlayerLoadEvent = new NexusPlayerLoadEvent(nexusPlayer, originalJoinMessage);
            Bukkit.getPluginManager().callEvent(nexusPlayerLoadEvent);
            String joinMessage = nexusPlayerLoadEvent.getJoinMessage();
            if (joinMessage != null) {
                Bukkit.broadcastMessage(MCUtils.color(joinMessage));
            }
            if (nexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
                StaffChat.sendJoin(nexusPlayer);
            }
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
    
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("minecraft:")) {
            if (!e.getPlayer().hasPermission("nexuscore.admin")) {
                e.setCancelled(true);
            }
        }
    }
}
