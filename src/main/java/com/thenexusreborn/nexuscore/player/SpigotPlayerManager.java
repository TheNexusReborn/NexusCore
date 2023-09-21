package com.thenexusreborn.nexuscore.player;

import com.starmediadev.starlib.util.TimeUnit;
import com.starmediadev.starsql.objects.Database;
import com.starmediadev.starsql.objects.Row;
import com.starmediadev.starsql.objects.Table;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameInfo;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.server.NetworkType;
import com.thenexusreborn.api.server.Phase;
import com.thenexusreborn.api.stats.StatHelper;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.thread.PlayerJoinThread;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;

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
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (NexusAPI.NETWORK_TYPE != NetworkType.SINGLE) {
            return;
        }

        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
        CachedPlayer cachedPlayer = playerManager.getCachedPlayer(e.getUniqueId());
        if (cachedPlayer == null) {
            NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                NexusPlayer nexusPlayer = createPlayerData(e.getUniqueId(), e.getName());
                NexusAPI.getApi().getNetworkManager().send("playercreate", nexusPlayer.getUniqueId().toString());
            });
        }

        if (cachedPlayer != null) {
            Punishment activePunishment = checkPunishments(cachedPlayer.getUniqueId());
            if (activePunishment != null) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "");
                e.setKickMessage(ChatColor.translateAlternateColorCodes('&', activePunishment.formatKick()));
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
        
        if (plugin.getSpawnpoint() != null) {
            player.teleport(plugin.getSpawnpoint());
        }

        Session session = new Session(player.getUniqueId());
        session.start();
        this.sessions.put(player.getUniqueId(), session);

        if (NexusAPI.NETWORK_TYPE == NetworkType.SINGLE) {
            if (!getPlayers().containsKey(player.getUniqueId())) {
                NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                    NexusPlayer nexusPlayer = null;
                    if (!hasData(player.getUniqueId())) {
                        nexusPlayer = createPlayerData(player.getUniqueId(), player.getName());
                    } else {
                        try {
                            nexusPlayer = NexusAPI.getApi().getPrimaryDatabase().get(NexusPlayer.class, "uniqueId", player.getUniqueId()).get(0);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (Bukkit.getPlayer(nexusPlayer.getUniqueId()) == null) {
                        return;
                    }

                    if (nexusPlayer.getFirstJoined() == 0) {
                        nexusPlayer.setFirstJoined(System.currentTimeMillis());
                    }
                    nexusPlayer.setLastLogin(System.currentTimeMillis());
                    this.loginTimes.put(nexusPlayer.getUniqueId(), System.currentTimeMillis());
                    if (!nexusPlayer.getName().equals(player.getName())) {
                        nexusPlayer.setName(player.getName());
                    }

                    if (NexusAPI.PHASE == Phase.ALPHA) {
                        if (!nexusPlayer.isAlpha()) {
                            nexusPlayer.setAlpha(true);
                        }
                    } else if (NexusAPI.PHASE == Phase.BETA) {
                        if (!nexusPlayer.isBeta()) {
                            nexusPlayer.setBeta(true);
                        }
                    }

                    NexusAPI.getApi().getPrimaryDatabase().saveSilent(nexusPlayer);

                    getPlayers().put(nexusPlayer.getUniqueId(), nexusPlayer);
                    cachedPlayers.put(nexusPlayer.getUniqueId(), new CachedPlayer(nexusPlayer));
                    InetSocketAddress socketAddress = player.getAddress();
                    String hostName = socketAddress.getHostString();
                    NexusAPI.getApi().getPlayerManager().addIpHistory(player.getUniqueId(), hostName);
                });
            }
        }

        new PlayerJoinThread(plugin, player).start();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        NexusPlayer nexusPlayer = this.players.get(e.getPlayer().getUniqueId());
        this.handlePlayerLeave(nexusPlayer);
        this.players.remove(e.getPlayer().getUniqueId());
        e.setQuitMessage(null);

        if (NexusAPI.NETWORK_TYPE == NetworkType.SINGLE) {
            NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                nexusPlayer.setLastLogout(System.currentTimeMillis());
                long playTime = System.currentTimeMillis() - this.loginTimes.get(nexusPlayer.getUniqueId());
                this.loginTimes.remove(nexusPlayer.getUniqueId());
                Session session = this.sessions.get(nexusPlayer.getUniqueId());
                if (session == null) {
                    plugin.getLogger().severe("There was no session for player " + nexusPlayer.getName());
                } else {
                    session.end();

                    if (session.getTimeOnline() >= TimeUnit.MINUTES.toMilliseconds(5)) {
                        Database database = NexusAPI.getApi().getPrimaryDatabase();
                        Table table = database.getTable(GameInfo.class);
                        String query = "select * from " + table.getName() + " where `gameStart`>='" + session.getStart() + "' and `gameEnd` <= '" + session.getEnd() + "' and `players` like '%" + nexusPlayer.getName() + "%';";
                        try {
                            List<Row> rows = database.executeQuery(query);
                            session.setGamesPlayed(rows.size());
                        } catch (SQLException ex) {
                            NexusAPI.getApi().getLogger().info(query);
                            ex.printStackTrace();
                        }

                        if (session.getGamesPlayed() > 0) {
                            database.saveSilent(session);
                        }
                    }
                }
                this.sessions.remove(nexusPlayer.getUniqueId());
                nexusPlayer.changeStat("playtime", playTime, StatOperator.ADD).push();
                StatHelper.consolidateStats(nexusPlayer);
                NexusAPI.getApi().getPrimaryDatabase().saveSilent(nexusPlayer);
            });
            this.players.remove(nexusPlayer.getUniqueId());
            if (nexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
                StaffChat.sendDisconnect(nexusPlayer);
            }
            this.handlePlayerLeave(nexusPlayer);
        }
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
