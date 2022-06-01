package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.network.cmd.*;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.api.server.*;
import com.thenexusreborn.nexuscore.anticheat.AnticheatManager;
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

public class NexusCore extends JavaPlugin {
    
    private Driver mysqlDriver;
    
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
        //blacklist
        
        PunishRemoveCommands prCmds = new PunishRemoveCommands(this);
        getCommand("unban").setExecutor(prCmds);
        getCommand("unmute").setExecutor(prCmds);
        //Unblacklist
    
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
                if (punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.WARN) {
                    //The Spigot Servers will handle only mutes and warnings as they are chat based. Proxy will handle bans, blacklists, and kicks
                    NexusAPI.getApi().getPunishmentManager().addPunishment(punishment);
                }
            }
        }.runTaskAsynchronously(this));
        
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