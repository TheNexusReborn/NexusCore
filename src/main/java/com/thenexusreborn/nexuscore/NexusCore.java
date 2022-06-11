package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.network.cmd.NetworkCommand;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Preference.Info;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.api.server.*;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.api.tournament.Tournament.ScoreInfo;
import com.thenexusreborn.nexuscore.anticheat.AnticheatManager;
import com.thenexusreborn.nexuscore.api.events.*;
import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.menu.MenuManager;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.proxy.ProxyMessageListener;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.nms.NMS;
import com.thenexusreborn.nexuscore.util.nms.NMS.Version;
import com.thenexusreborn.nexuscore.util.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class NexusCore extends JavaPlugin {
    
    private NMS nms;
    
    private ChatManager chatManager;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
    
        NexusAPI.setApi(new SpigotNexusAPI(this));
        try {
            NexusAPI.getApi().init();
        } catch (Exception e) {
            getLogger().severe("Error while enabling the NexusAPI. Disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
            e.printStackTrace();
            return;
        }
        
        nms = NMS.getNMS(Version.MC_1_8_R3);
        
        Updater updater = new Updater(this);
        Bukkit.getServer().getScheduler().runTaskTimer(this, updater, 1L, 1L);
        
        chatManager = new ChatManager(this);
    
        Info vanishInfo = new Info("vanish", "Vanish", "A staff only thing where you can be completely invisible", false);
        vanishInfo.setHandler((preference, player, oldValue, newValue) -> Bukkit.getPluginManager().callEvent(new VanishToggleEvent(player, oldValue, newValue)));
        NexusAPI.getApi().getDataManager().registerPreference(vanishInfo);
        Info incognitoInfo = new Info("incognito", "Incognito", "A media+ thing where you can be hidden from others", false);
        incognitoInfo.setHandler((preference, player, oldValue, newValue) -> Bukkit.getPluginManager().callEvent(new IncognitoToggleEvent(player, oldValue, newValue)));
        NexusAPI.getApi().getDataManager().registerPreference(incognitoInfo);
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "nexus");
    
        ProxyMessageListener messageListener = new ProxyMessageListener(this);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", messageListener);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "nexus", messageListener);
        
        Bukkit.getServer().getPluginManager().registerEvents((SpigotPlayerManager) NexusAPI.getApi().getPlayerManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(chatManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(new MenuManager(), this);
        
        registerCommand("rank", new RankCommand(this));
        registerCommand("setstat", new SetStatCmd(this));
        registerCommand("consolodatestats", new ConsolodateStatsCmd(this));
        getCommand("tag").setExecutor(new TagCommand(this));
        getCommand("say").setExecutor(new SayCommand(this));
        getCommand("message").setExecutor(new MessageCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("me").setExecutor(new MeCommand());
        getCommand("discord").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(MCUtils.color(MsgType.INFO + "Discord: &bhttps://discord.gg/bawZKSWEpT"));
            return true;
        });
    
        getCommand("shop").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(MCUtils.color(MsgType.INFO + "Shop: &bhttps://shop.thenexusreborn.com/"));
            return true;
        });
        
        PunishmentCommands puCmds = new PunishmentCommands(this);
        getCommand("ban").setExecutor(puCmds);
        getCommand("tempban").setExecutor(puCmds);
        getCommand("mute").setExecutor(puCmds);
        getCommand("tempmute").setExecutor(puCmds);
        getCommand("kick").setExecutor(puCmds);
        getCommand("warn").setExecutor(puCmds);
        getCommand("blacklist").setExecutor(puCmds);
        
        PunishRemoveCommands prCmds = new PunishRemoveCommands(this);
        getCommand("unban").setExecutor(prCmds);
        getCommand("unmute").setExecutor(prCmds);
        getCommand("pardon").setExecutor(prCmds);
        getCommand("unblacklist").setExecutor(prCmds);
        
        PunishmentHistoryCmds phCmds = new PunishmentHistoryCmds(this);
        getCommand("history").setExecutor(phCmds);
        getCommand("staffhistory").setExecutor(phCmds);
        
        getCommand("alts").setExecutor(new AltsCommand(this));
        
        ToggleCmds toggleCmds = new ToggleCmds(this);
        getCommand("incognito").setExecutor(toggleCmds);
        getCommand("vanish").setExecutor(toggleCmds);
        
        getCommand("tournament").setExecutor(new TournamentCommand(this));
    
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
                    if (nexusPlayer != null) {
                        if (nexusPlayer.getScoreboard() != null) {
                            nexusPlayer.getScoreboard().update();
                        }
                        ActionBar actionBar = ((SpigotNexusPlayer) nexusPlayer).getActionBar();
                        actionBar.send(player);
                    }
                }
            }
        }.runTaskTimer(this, 1L, 1L);
    
        ServerInfo currentServer = NexusAPI.getApi().getServerManager().getCurrentServer();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
                    if (nexusPlayer != null) {
                        if (nexusPlayer.getScoreboard() != null) {
                            TablistHandler tablistHandler = nexusPlayer.getScoreboard().getTablistHandler();
                            if (tablistHandler != null) {
                                tablistHandler.update();
                            }
                        }
                    }
                }
                
                List<ServerInfo> allServers = NexusAPI.getApi().getDataManager().getAllServers();
                ServerManager serverManager = NexusAPI.getApi().getServerManager();
                for (ServerInfo server : new ArrayList<>(serverManager.getServers())) {
                    NexusAPI.getApi().getDataManager().updateServerInfo(server);
                }
    
                for (ServerInfo server : allServers) {
                    if (!serverManager.getServers().contains(server)) {
                        serverManager.addServer(server);
                    }
                }
    
                currentServer.setStatus("online");
                currentServer.setPlayers(Bukkit.getOnlinePlayers().size());
                NexusAPI.getApi().getDataManager().pushServerInfo(currentServer);
            }
        }.runTaskTimerAsynchronously(this, 1L, 20L);
        
        NexusAPI.getApi().getNetworkManager().addCommand(new NetworkCommand("staffchat", (StaffChat::handleIncoming)));
        NexusAPI.getApi().getNetworkManager().getCommand("punishment").setExecutor((cmd, origin, args) -> new BukkitRunnable() {
            @Override
            public void run() {
                int id = Integer.parseInt(args[0]);
                Punishment punishment = NexusAPI.getApi().getDataManager().getPunishment(id);
                if (punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.WARN || punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.BLACKLIST) {
                    NexusAPI.getApi().getPunishmentManager().addPunishment(punishment);
    
                    Player player = Bukkit.getPlayer(UUID.fromString(punishment.getTarget()));
                    if (player != null && punishment.isActive()) {
                        if (punishment.getType() == PunishmentType.MUTE) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You have been muted by " + punishment.getActorNameCache() + " for " + punishment.getReason() + ". (" + punishment.formatTimeLeft() + ")"));
                        } else if (punishment.getType() == PunishmentType.WARN) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You have been warned by " + punishment.getActorNameCache() + " for " + punishment.getReason() + "."));
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You must type the code " + punishment.getAcknowledgeInfo().getCode() + " in chat before you can speak again."));
                        }
                    }
                }
            }
        }.runTaskAsynchronously(this));
    
        new BukkitRunnable() {
            private Map<UUID, Integer> scores = new HashMap<>();
            @Override
            public void run() {
                Tournament tournament = NexusAPI.getApi().getTournament();
                if (tournament == null) {
                    return;
                }
                
                try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery("select * from stats where name='sg_tournament_points';");
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        int value = Integer.parseInt(resultSet.getString("value"));
                        if (value != 0) {
                            scores.put(uuid, value);
                        }
                    }
                    
                    resultSet = statement.executeQuery("select * from statchanges where statName='sg_tournament_points';");
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        int value = Integer.parseInt(resultSet.getString("value"));
                        if (value != 0) {
                            if (scores.containsKey(uuid)) {
                                scores.put(uuid, scores.get(uuid) + value);
                            } else {
                                scores.put(uuid, value);
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
    
                Iterator<Entry<UUID, Integer>> iterator = scores.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<UUID, Integer> entry = iterator.next();
                    ScoreInfo scoreInfo = tournament.getScoreCache().get(entry.getKey());
                    if (scoreInfo != null) {
                        scoreInfo.setScore(entry.getValue());
                        scoreInfo.setLastUpdated(System.currentTimeMillis());
                        iterator.remove();
                    }
                }
    
                try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
                    for (Entry<UUID, Integer> entry : scores.entrySet()) {
                        String name = null;
                        Player player = Bukkit.getPlayer(entry.getKey());
                        if (player != null) {
                            name = player.getName();
                        } else {
                            ResultSet resultSet = statement.executeQuery("select lastKnownName from players where uuid='" + entry.getKey().toString() + "';");
                            if (resultSet.next()) {
                                name = resultSet.getString("lastKnownName");
                            }
                        }
                        
                        if (name != null) {
                            ScoreInfo scoreInfo = new ScoreInfo(entry.getKey(), name, entry.getValue());
                            scoreInfo.setLastUpdated(System.currentTimeMillis());
                            tournament.getScoreCache().put(scoreInfo.getUuid(), scoreInfo);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                
                scores.clear();
            }
        }.runTaskTimerAsynchronously(this, 20L, 1200L);
        
        getServer().getPluginManager().registerEvents(new AnticheatManager(), this);
    }
    
    @Override
    public void onDisable() {
        NexusAPI.getApi().getPlayerManager().saveData();
        ServerInfo serverInfo = NexusAPI.getApi().getServerManager().getCurrentServer();
        serverInfo.setStatus("offline");
        serverInfo.setState("none");
        serverInfo.setPlayers(0);
        NexusAPI.getApi().getDataManager().pushServerInfo(serverInfo);
        NexusAPI.getApi().getNetworkManager().close();
    }
    
    private void registerCommand(String cmd, TabExecutor tabExecutor) {
        PluginCommand command = getCommand(cmd);
        command.setExecutor(tabExecutor);
        command.setTabCompleter(tabExecutor);
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    public NMS getNMS() {
        return nms;
    }
    
    public Connection getConnection(String database) throws SQLException {
        String url = "jdbc:mysql://" + getConfig().getString("mysql.host") + "/" + database + "?user=" + getConfig().getString("mysql.user") + "&password=" + getConfig().getString("mysql.password");
        return DriverManager.getConnection(url);
    }
    
    public Connection getConnection() throws SQLException {
        return getConnection(getConfig().getString("mysql.database"));
    }
}