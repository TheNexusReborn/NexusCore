package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.util.StaffChat;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class SpigotPlayerManager extends PlayerManager implements Listener {
    
    private final NexusCore plugin;
    
    private final Map<UUID, Integer> clicksPerSecond = Collections.synchronizedMap(new HashMap<>());
    
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
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entry<UUID, Integer> entry : new HashMap<>(clicksPerSecond).entrySet()) {
                    UUID uuid = entry.getKey();
                    int cps = entry.getValue();
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        clicksPerSecond.remove(uuid);
                        continue;
                    }
                    
                    if (cps > 16) {
                        player.sendMessage(MCUtils.color(MsgType.WARN + "You are clicking at " + cps + " and the server limit is 16. If you do this too long, you may get auto-banned by the anti-cheat."));
                    }
                    
                    clicksPerSecond.put(uuid, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (clicksPerSecond.containsKey(e.getPlayer().getUniqueId())) {
                int clicks = clicksPerSecond.get(e.getPlayer().getUniqueId()) + 1;
                this.clicksPerSecond.put(e.getPlayer().getUniqueId(), clicks);
                if (clicks > 16) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String originalJoinMessage = e.getJoinMessage();
        e.setJoinMessage(null);
        if (!players.containsKey(e.getPlayer().getUniqueId())) {
            IActionBar actionBar = () -> "&cPlease wait while your data is being loaded.";
            SpigotUtils.sendActionBar(e.getPlayer(), actionBar.getText());
            BukkitRunnable abr = new BukkitRunnable() {
                @Override
                public void run() {
                    SpigotUtils.sendActionBar(e.getPlayer(), actionBar.getText());
                }
            };
            abr.runTaskTimer(plugin, 20L, 20L);
            getNexusPlayerAsync(e.getPlayer().getUniqueId(), nexusPlayer -> {
                if (Bukkit.getPlayer(e.getPlayer().getUniqueId()) == null) {
                    return;
                }
                abr.cancel();
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
                
                if (nexusPlayer.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    Player player = e.getPlayer();
                    player.addAttachment(plugin, "spartan.info", true);
                    player.addAttachment(plugin, "spartan.notifications", true);
                }
                
                this.clicksPerSecond.put(e.getPlayer().getUniqueId(), 0);
                
                nexusPlayer.setActionBar(() -> "&aYour data has been loaded...");
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        nexusPlayer.setActionBar(null);
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
        this.handlePlayerLeave(nexusPlayer);
        this.players.remove(e.getPlayer().getUniqueId());
        e.setQuitMessage(null);
        this.clicksPerSecond.remove(e.getPlayer().getUniqueId());
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
