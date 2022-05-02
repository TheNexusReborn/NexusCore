package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.menu.MenuManager;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.proxy.ProxyMessageListener;
import com.thenexusreborn.nexuscore.util.ActionBar;
import com.thenexusreborn.nexuscore.util.command.CommandManager;
import com.thenexusreborn.nexuscore.util.nms.NMS;
import com.thenexusreborn.nexuscore.util.nms.NMS.Version;
import com.thenexusreborn.nexuscore.util.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class NexusCore extends JavaPlugin {
    
    private Driver mysqlDriver;
    
    private NMS nms;
    
    private CommandManager commandManager;
    private ChatManager chatManager;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        
        nms = NMS.getNMS(Version.MC_1_8_R3);
        
        Updater updater = new Updater(this);
        Bukkit.getServer().getScheduler().runTaskTimer(this, updater, 1L, 1L);
        
        commandManager = new CommandManager(this);
        chatManager = new ChatManager(this);
    
        NexusAPI.setApi(new SpigotNexusAPI(this));
        try {
            NexusAPI.getApi().init();
        } catch (Exception e) {
            getLogger().severe("Error while enabling the NexusAPI. Disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
            e.printStackTrace();
            return;
        }
        
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
        
        
    }
    
    @Override
    public void onDisable() {
        NexusAPI.getApi().getPlayerManager().saveData();
        ServerInfo serverInfo = NexusAPI.getApi().getServerManager().getCurrentServer();
        serverInfo.setStatus("offline");
        serverInfo.setState("none");
        serverInfo.setPlayers(0);
        NexusAPI.getApi().getDataManager().pushServerInfo(serverInfo);
    }
    
    private void registerCommand(String cmd, TabExecutor tabExecutor) {
        PluginCommand command = getCommand(cmd);
        command.setExecutor(tabExecutor);
        command.setTabCompleter(tabExecutor);
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    public NMS getNMS() {
        return nms;
    }
    
    public Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + getConfig().getString("mysql.host") + "/" + getConfig().getString("mysql.database") + "?user=" + getConfig().getString("mysql.user") + "&password=" + getConfig().getString("mysql.password");
        return DriverManager.getConnection(url);
    }
}