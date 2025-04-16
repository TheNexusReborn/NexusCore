package com.thenexusreborn.nexuscore.player;

import com.stardevllc.clock.clocks.Stopwatch;
import com.stardevllc.helper.StringHelper;
import com.stardevllc.mojang.MojangAPI;
import com.stardevllc.mojang.MojangProfile;
import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.skins.Skin;
import com.stardevllc.starcore.skins.SkinManager;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameInfo;
import com.thenexusreborn.api.nickname.Nickname;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.ScoreboardView;
import com.thenexusreborn.api.sql.objects.*;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.api.util.NetworkType;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.scoreboard.SpigotNexusScoreboard;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

public class SpigotPlayerManager extends PlayerManager implements Listener {

    private final NexusCore plugin;

    public SpigotPlayerManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        UUID uniqueId = e.getUniqueId();

        Punishment punishment = checkPunishments(uniqueId);
        if (punishment != null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', punishment.formatKick()));
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

            PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();

            Punishment activePunishment = checkPunishments(e.getPlayer().getUniqueId());
            if (activePunishment != null) {
                e.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', activePunishment.formatKick()));
                return;
            }

            if (!getPlayers().containsKey(player.getUniqueId())) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    NexusPlayer nexusPlayer;
                    if (!getUuidNameMap().containsKey(player.getUniqueId())) {
                        nexusPlayer = createPlayerData(player.getUniqueId(), player.getName());
                    } else {
                        try {
                            nexusPlayer = NexusAPI.getApi().getPrimaryDatabase().get(NexusPlayer.class, "uniqueId", player.getUniqueId()).getFirst();
                        } catch (SQLException ex) {
                            nexusPlayer = null;
                            ex.printStackTrace();
                        }
                    }

                    if (Bukkit.getPlayer(nexusPlayer.getUniqueId()) == null) {
                        return;
                    }
                    
                    MojangProfile mojangProfile = MojangAPI.getProfile(player.getUniqueId());
                    nexusPlayer.setMojangProfile(mojangProfile);

                    nexusPlayer.setSession(session);
                    playerManager.getUuidNameMap().forcePut(nexusPlayer.getUniqueId(), new Name(nexusPlayer.getName()));
                    playerManager.getUuidRankMap().put(nexusPlayer.getUniqueId(), nexusPlayer.getRanks());

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
                        player.sendMessage(StarColors.color(MsgType.ERROR + "Failed to save your player data to the database. Please report as a bug and try to re-log."));
                        ex.printStackTrace();
                    }

                    getPlayers().put(nexusPlayer.getUniqueId(), nexusPlayer);
                    InetSocketAddress socketAddress = player.getAddress();
                    String hostName = socketAddress.getHostString();
                    NexusAPI.getApi().getPlayerManager().addIpHistory(player.getUniqueId(), hostName);
                    
                    Nickname nickname = nexusPlayer.getNickname();
                    SkinManager skinManager = Bukkit.getServicesManager().getRegistration(SkinManager.class).getProvider();
                    Skin skin = (nickname != null && nickname.getSkin() != null && !nickname.getSkin().isBlank()) ? skinManager.getFromMojang(UUID.fromString(nickname.getSkin())) : null;

                    NexusPlayer finalNexusPlayer = nexusPlayer;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (skin != null && nickname.isActive()) {
                            plugin.getNickWrapper().setNick(plugin, player, nickname.getName(), skin);
                        }
                        
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
                        if (joinMessage != null && !joinMessage.isEmpty()) {
                            Bukkit.broadcastMessage(StarColors.color(joinMessage));
                        }

                        NexusAPI.getApi().getPlayerManager().getPlayers().put(finalNexusPlayer.getUniqueId(), finalNexusPlayer);

                        SpigotUtils.sendActionBar(player, "&aYour data has been loaded");

                        plugin.getNexusServer().join(finalNexusPlayer);

                        Stopwatch playtimeStopwatch = plugin.getClockManager().createStopwatch(Long.MAX_VALUE);
                        playtimeStopwatch.addRepeatingCallback(stopwatchSnapshot -> {
                            if (finalNexusPlayer.getToggleValue("vanish")) {
                                return;
                            }

                            if (stopwatchSnapshot.getTime() == 0) {
                                return; //Prevent this running immediately when a player joins
                            }

                            Rank rank = finalNexusPlayer.getRank();
                            double xp = 10 * rank.getMultiplier();

                            DecimalFormat format = new DecimalFormat(Constants.NUMBER_FORMAT);

                            String bonusMessage = "";
                            if (rank.getMultiplier() > 1) {
                                bonusMessage = rank.getColor() + "&l x" + format.format(rank.getMultiplier()) + " " + StringHelper.titlize(rank.name()) + " Bonus";
                            }

                            finalNexusPlayer.sendMessage(MsgType.INFO + "&d+" + format.format(xp) + "XP (Playtime) " + bonusMessage);
                            finalNexusPlayer.addXp(xp);
                        }, TimeUnit.MINUTES.toMillis(10));
                        playtimeStopwatch.start();

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
        e.setQuitMessage(null);
        
        if (nexusPlayer == null) {
            plugin.getLogger().warning("Player " + e.getPlayer().getUniqueId() + " quit, but did not have data loaded");
            plugin.getNexusServer().quit(e.getPlayer().getUniqueId());
            return; //Probably joined then left before it could be fully loaded
        }

        if (NexusAPI.NETWORK_TYPE == NetworkType.SINGLE) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Session session = nexusPlayer.getSession();
                session.end();
                nexusPlayer.setLastLogout(session.getEnd());
                long playTime = session.getTimeOnline();
                SQLDatabase database = NexusAPI.getApi().getPrimaryDatabase();
                Table table = database.getTable(GameInfo.class);
                String query = "select * from " + table.getName() + " where `gameStart`>='" + session.getStart() + "' and `gameEnd` <= '" + session.getEnd() + "' and `players` like '%" + e.getPlayer().getName() + "%';";
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
                nexusPlayer.getPlayerTime().addPlaytime(playTime);
                NexusAPI.getApi().getPrimaryDatabase().saveSilent(nexusPlayer);
            });
            this.players.remove(nexusPlayer.getUniqueId());
            this.plugin.getNexusServer().quit(nexusPlayer);
            if (nexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
                if (!nexusPlayer.isNicked()) { //TODO Need a change from StarChat to filter receivers
                    plugin.getStaffChannel().sendMessage(new ChatContext(nexusPlayer.getTrueDisplayName() + " &7disconnected"));
                }
            }
        }
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("me")) {
            e.setCancelled(true);
        } else if (e.getMessage().toLowerCase().startsWith("minecraft:tell") || e.getMessage().toLowerCase().startsWith("minecraft:whisper")) {
            e.setCancelled(true);
        }
    }
}
