package com.thenexusreborn.nexuscore.player;

import com.stardevllc.clock.clocks.Stopwatch;
import com.stardevllc.mojang.MojangAPI;
import com.stardevllc.mojang.MojangProfile;
import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starcore.api.Skin;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.skins.SkinManager;
import com.stardevllc.time.TimeUnit;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.nickname.Nickname;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.ScoreboardView;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.api.util.NetworkType;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.reflection.impl.ActionBar;
import com.thenexusreborn.nexuscore.scoreboard.SpigotNexusScoreboard;
import com.thenexusreborn.nexuscore.scoreboard.impl.RankTablistHandler;
import com.thenexusreborn.nexuscore.util.MsgType;
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
import java.util.*;

public class SpigotPlayerManager extends PlayerManager implements Listener {
    
    private static final DecimalFormat PLAYTIME_XP_FORMAT = new DecimalFormat(Constants.NUMBER_FORMAT);

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
        Punishment activePunishment = checkPunishments(player.getUniqueId());
        if (activePunishment != null) {
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', activePunishment.formatKick()));
            return;
        }
        for (Player o : Bukkit.getOnlinePlayers()) {
            o.hidePlayer(player);

            NexusPlayer p = getNexusPlayer(o.getUniqueId());
            if (p == null) {
                player.hidePlayer(o);
            }
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final NexusPlayer nexusPlayer;
            if (!this.players.containsKey(player.getUniqueId())) {
                if (!getUuidNameMap().containsKey(player.getUniqueId())) {
                    nexusPlayer = createPlayerData(player.getUniqueId(), player.getName());
                    NexusReborn.getPrimaryDatabase().saveSilent(nexusPlayer);
                } else {
                    try {
                        nexusPlayer = NexusReborn.getPrimaryDatabase().get(NexusPlayer.class, "uniqueId", player.getUniqueId()).getFirst();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        return;
                    }
                }
            } else {
                nexusPlayer = this.players.get(player.getUniqueId());
            }
            
            if (nexusPlayer == null || Bukkit.getPlayer(nexusPlayer.getUniqueId()) == null) {
                return;
            }
            
            getPlayers().put(nexusPlayer.getUniqueId(), nexusPlayer);
            
            Session session;
            if (NexusReborn.NETWORK_TYPE == NetworkType.SINGLE) {
                session = new Session(player.getUniqueId());
                session.start();
            } else {
                try {
                    session = NexusReborn.getPrimaryDatabase().get(Session.class, "uniqueid", player.getUniqueId()).getFirst();
                } catch (Throwable ex) {
                    session = new Session(player.getUniqueId());
                    session.start();
                    NexusReborn.getPrimaryDatabase().saveSilent(session);
                }
            }
            
            nexusPlayer.setSession(session);
            
            MojangProfile mojangProfile = MojangAPI.getProfile(player.getUniqueId());
            nexusPlayer.setMojangProfile(mojangProfile);
            
            getUuidNameMap().forcePut(nexusPlayer.getUniqueId(), new Name(nexusPlayer.getName()));
            getUuidRankMap().put(nexusPlayer.getUniqueId(), nexusPlayer.getRanks());
            
            if (nexusPlayer.getFirstJoined() == 0) {
                nexusPlayer.setFirstJoined(session.getStart());
            }
            
            nexusPlayer.setLastLogin(session.getStart());
            
            if (!nexusPlayer.getName().equals(player.getName())) {
                nexusPlayer.setName(player.getName());
            }
            
            try {
                NexusReborn.getPrimaryDatabase().save(nexusPlayer);
            } catch (SQLException ex) {
                player.sendMessage(StarColors.color(MsgType.ERROR + "Failed to save your player data to the database. Please report as a bug and try to re-log."));
                ex.printStackTrace();
            }
            
            Stopwatch playtimeStopwatch = plugin.getClockManager().createStopwatch(Long.MAX_VALUE);
            UUID rewardID = playtimeStopwatch.addRepeatingCallback(stopwatchSnapshot -> {
                if (nexusPlayer.getPlayTimeStopwatch() == null) {
                    return;
                }
                
                if (!playtimeStopwatch.getUniqueId().equals(nexusPlayer.getPlayTimeStopwatch().getUniqueId())) {
                    playtimeStopwatch.cancel();
                    return;
                }
                
                if (nexusPlayer.getToggleValue("vanish")) {
                    return;
                }
                
                if (stopwatchSnapshot.getTime() == 0) {
                    return; //Prevent this running immediately when a player joins
                }
                
                double xp = 10;
                
                nexusPlayer.sendMessage(MsgType.INFO + "&d+" + PLAYTIME_XP_FORMAT.format(xp) + "XP (Playtime)");
                nexusPlayer.addXp(xp);
            }, TimeUnit.MINUTES.toMillis(10));
            playtimeStopwatch.start();
            nexusPlayer.setPlayTimeStopwatch(playtimeStopwatch);
            nexusPlayer.setPlaytimeRewardId(rewardID);
            
            InetSocketAddress socketAddress = player.getAddress();
            String hostName = socketAddress.getHostString();
            NexusReborn.getPlayerManager().addIpHistory(player.getUniqueId(), hostName);
            
            Nickname nickname = nexusPlayer.getNickname();
            SkinManager skinManager = Bukkit.getServicesManager().getRegistration(SkinManager.class).getProvider();
            Skin skin = nickname != null && nickname.getSkin() != null && !nickname.getSkin().isBlank() ? skinManager.getFromMojang(nickname.getSkin()) : null;
            
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (skin != null && nickname.isActive()) {
                    plugin.getNickWrapper().setNick(plugin, player, nickname.getName(), skin);
                }
                
                NexusScoreboard scoreboard = new SpigotNexusScoreboard(nexusPlayer);
                scoreboard.init();
                
                nexusPlayer.setScoreboard(scoreboard);
                
                NexusPlayerLoadEvent loadEvent = new NexusPlayerLoadEvent(nexusPlayer);
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
                
                ActionBar actionBar = new ActionBar();
                actionBar.send(player, "&aYour data has been loaded!");
                
                plugin.getNexusServer().join(nexusPlayer);
                
                if (loadEvent.getActionBar() != null) {
                    new BukkitRunnable() {
                        public void run() {
                            nexusPlayer.setActionBar(loadEvent.getActionBar());
                        }
                    }.runTaskLater(plugin, 60L);
                }
                
                addOnlinePlayer(player.getUniqueId());
            });
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        removeOnlinePlayer(e.getPlayer().getUniqueId());
        NexusPlayer nexusPlayer = this.players.get(e.getPlayer().getUniqueId());
        e.setQuitMessage(null);
        
        if (nexusPlayer == null) {
            plugin.getLogger().warning("Player " + e.getPlayer().getUniqueId() + " quit, but did not have data loaded");
            plugin.getNexusServer().quit(e.getPlayer().getUniqueId());
            return; //Probably joined then left before it could be fully loaded
        }
        
        Stopwatch playTimeStopwatch = nexusPlayer.getPlayTimeStopwatch();
        if (playTimeStopwatch != null) {
            playTimeStopwatch.cancel();
            nexusPlayer.setPlayTimeStopwatch(null);
            nexusPlayer.setPlaytimeRewardId(null);
        }
        
        if (NexusReborn.NETWORK_TYPE == NetworkType.SINGLE) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Session session = nexusPlayer.getSession();
                session.end();
                nexusPlayer.setLastLogout(session.getEnd());
                long playTime = session.getTimeOnline();
                NexusReborn.getPrimaryDatabase().deleteSilent(session);
                nexusPlayer.setSession(null);
                nexusPlayer.getPlayerTime().addPlaytime(playTime);
                NexusReborn.getPrimaryDatabase().saveSilent(nexusPlayer);
            });
        }
        
        this.players.remove(nexusPlayer.getUniqueId());
        this.plugin.getNexusServer().quit(nexusPlayer);
        if (nexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
            if (!nexusPlayer.isNicked()) { //TODO Need a change from StarChat to filter receivers
                plugin.getStaffChannel().sendMessage(new ChatContext(nexusPlayer.getTrueDisplayName() + " &7disconnected"));
            }
        }
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("minecraft:me")) {
            e.setCancelled(true);
        } else if (e.getMessage().toLowerCase().startsWith("minecraft:tell") || e.getMessage().toLowerCase().startsWith("minecraft:whisper")) {
            e.setCancelled(true);
        }
    }
}
