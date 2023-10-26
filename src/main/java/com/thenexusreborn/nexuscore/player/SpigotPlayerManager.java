package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameInfo;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.ScoreboardView;
import com.thenexusreborn.api.server.NetworkType;
import com.thenexusreborn.api.stats.StatHelper;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.scoreboard.SpigotNexusScoreboard;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import me.firestar311.starsql.api.objects.Row;
import me.firestar311.starsql.api.objects.SQLDatabase;
import me.firestar311.starsql.api.objects.Table;
import net.md_5.bungee.api.ChatColor;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        if (NexusAPI.NETWORK_TYPE == NetworkType.SINGLE) {
            Session session = new Session(player.getUniqueId());
            session.start();
            this.sessions.put(player.getUniqueId(), session);

            PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
            CachedPlayer cachedPlayer = playerManager.getCachedPlayer(e.getPlayer().getUniqueId());

            if (cachedPlayer != null) {
                Punishment activePunishment = checkPunishments(cachedPlayer.getUniqueId());
                if (activePunishment != null) {
                    e.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', activePunishment.formatKick()));
                    return;
                }
            }

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

                    nexusPlayer.setSession(session);

                    if (nexusPlayer.getFirstJoined() == 0) {
                        nexusPlayer.setFirstJoined(System.currentTimeMillis());
                    }
                    nexusPlayer.setLastLogin(System.currentTimeMillis());
                    if (!nexusPlayer.getName().equals(player.getName())) {
                        nexusPlayer.setName(player.getName());
                    }

                    try {
                        NexusAPI.getApi().getPrimaryDatabase().save(nexusPlayer);
                    } catch (SQLException ex) {
                        player.sendMessage(MCUtils.color(MsgType.ERROR + "Failed to save your player data to the database. Please report as a bug and try to re-log."));
                        ex.printStackTrace();
                        return;
                    }

                    getPlayers().put(nexusPlayer.getUniqueId(), nexusPlayer);
                    cachedPlayers.put(nexusPlayer.getUniqueId(), new CachedPlayer(nexusPlayer));
                    InetSocketAddress socketAddress = player.getAddress();
                    String hostName = socketAddress.getHostString();
                    NexusAPI.getApi().getPlayerManager().addIpHistory(player.getUniqueId(), hostName);

                    NexusPlayer finalNexusPlayer = nexusPlayer;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        NexusScoreboard scoreboard = new SpigotNexusScoreboard(finalNexusPlayer);
                        scoreboard.init();

                        finalNexusPlayer.setScoreboard(scoreboard);

                        NexusPlayerLoadEvent loadEvent = new NexusPlayerLoadEvent(finalNexusPlayer);
                        try {
                            Bukkit.getServer().getPluginManager().callEvent(loadEvent);
                        } catch (Exception ex) {
                            plugin.getLogger().severe("There was an error handling the NexusPlayerLoadEvent. This was caught to prevent other issues. Stack Trace Below");
                            ex.printStackTrace();
                        }

                        ScoreboardView scoreboardView = loadEvent.getScoreboardView();
                        if (scoreboardView != null) {
                            scoreboard.setView(scoreboardView);
                        }

                        if (scoreboard.getTablistHandler() == null) {
                            if (loadEvent.getTablistHandler() != null) {
                                scoreboard.setTablistHandler(loadEvent.getTablistHandler());
                            } else {
                                scoreboard.setTablistHandler(new RankTablistHandler(scoreboard));
                            }
                        }

                        scoreboard.apply();

                        String joinMessage = loadEvent.getJoinMessage();
                        if (joinMessage != null && !joinMessage.equals("")) {
                            Bukkit.broadcastMessage(MCUtils.color(joinMessage));
                        }

                        if (finalNexusPlayer.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                            player.addAttachment(plugin, "spartan.info", true);
                            player.addAttachment(plugin, "spartan.notifications", true);
                        }

                        NexusAPI.getApi().getPlayerManager().getPlayers().put(finalNexusPlayer.getUniqueId(), finalNexusPlayer);

                        SpigotUtils.sendActionBar(player, "&aYour data has been loaded");

                        if (finalNexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
                            StaffChat.sendJoin(finalNexusPlayer);
                        }

                        if (loadEvent.getActionBar() != null) {
                            new BukkitRunnable() {
                                public void run() {
                                    finalNexusPlayer.setActionBar(loadEvent.getActionBar());
                                }
                            }.runTaskLater(plugin, 60L);
                        }
                    }, 1L);
                });
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        NexusPlayer nexusPlayer = this.players.get(e.getPlayer().getUniqueId());
        this.players.remove(e.getPlayer().getUniqueId());
        e.setQuitMessage(null);

        if (NexusAPI.NETWORK_TYPE == NetworkType.SINGLE) {
            NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                Session session = nexusPlayer.getSession();
                session.end();
                nexusPlayer.setLastLogout(session.getEnd());
                long playTime = session.getTimeOnline();
                SQLDatabase database = NexusAPI.getApi().getPrimaryDatabase();
                Table table = database.getTable(GameInfo.class);
                String query = "select * from " + table.getName() + " where `gameStart`>='" + session.getStart() + "' and `gameEnd` <= '" + session.getEnd() + "' and `players` like '%" + nexusPlayer.getName() + "%';";
                try {
                    List<Row> rows = database.executeQuery(query);
                    session.setGamesPlayed(rows.size());
                } catch (SQLException ex) {
                    NexusAPI.getApi().getLogger().info(query);
                    ex.printStackTrace();
                }

                if (session.getGamesPlayed() > 0 || session.getTimeOnline() >= TimeUnit.MINUTES.toMillis(1)) {
                    database.saveSilent(session);
                }
                nexusPlayer.setSession(null);
                nexusPlayer.changeStat("playtime", playTime, StatOperator.ADD);
                for (StatChange change : nexusPlayer.getStats().findAllChanges()) {
                    if (change.getId() != 0) {
                        change.push();
                    }
                }
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
