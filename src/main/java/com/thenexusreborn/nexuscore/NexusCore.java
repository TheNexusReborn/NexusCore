package com.thenexusreborn.nexuscore;

import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.stats.StatRegistry;
import com.thenexusreborn.nexuscore.tags.TagManager;
import com.thenexusreborn.nexuscore.util.command.CommandManager;
import com.thenexusreborn.nexuscore.util.nms.NMS;
import com.thenexusreborn.nexuscore.util.nms.NMS.Version;
import com.thenexusreborn.nexuscore.util.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.sql.*;

public class NexusCore extends JavaPlugin {
    
    private Driver mysqlDriver;
    
    private NMS nms;
    
    private CommandManager commandManager;
    private PlayerManager playerManager;
    private ChatManager chatManager;
    private TagManager tagManager;
    
    public static final Environment ENVIRONMENT = Environment.EXPERIMENTAL;
    
    @Override
    public void onEnable() {
        try {
            this.mysqlDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(this.mysqlDriver);
        } catch (SQLException e) {
            getLogger().severe("Error while loading the MySQL driver, disabling plugin");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        this.saveDefaultConfig();
        
        nms = NMS.getNMS(Version.MC_1_8_R3);
        
        Updater updater = new Updater(this);
        Bukkit.getServer().getScheduler().runTaskTimer(this, updater, 1L, 1L);
        
        commandManager = new CommandManager(this);
        playerManager = new PlayerManager(this);
        tagManager = new TagManager(this);
        chatManager = new ChatManager(this);
        
        try {
            playerManager.setupMysql();
        } catch (SQLException e) {
            getLogger().severe("Error while setting up Player MySQL Database");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        Bukkit.getServer().getPluginManager().registerEvents(playerManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(chatManager, this);
        
        registerCommand("rank", new RankCommand(this));
        registerCommand("setstat", new SetStatCmd(this));
        registerCommand("consolodatestats", new ConsolodateStatsCmd(this));
    
        StatRegistry.registerDoubleStat("nexites", 0);
        StatRegistry.registerDoubleStat("credits", 0);
        StatRegistry.registerDoubleStat("xp", 0);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    NexusPlayer nexusPlayer = playerManager.getNexusPlayer(player.getUniqueId());
                    if (nexusPlayer != null) {
                        if (nexusPlayer.getScoreboard() != null) {
                            for (Player other : Bukkit.getOnlinePlayers()) {
                                NexusPlayer otherNexusPlayer = playerManager.getNexusPlayer(other.getUniqueId());
                                if (otherNexusPlayer != null) {
                                    Team otherTeam = nexusPlayer.getScoreboard().getPlayerTeams().get(otherNexusPlayer.getUniqueId());
                                    String correctChar = NexusScoreboard.BEGIN_CHARS.get(nexusPlayer.getRank());
                                    if (otherTeam == null) {
                                        nexusPlayer.getScoreboard().createPlayerTeam(otherNexusPlayer);
                                    } else {
                                        if (otherTeam.getName().startsWith(correctChar)) {
                                            nexusPlayer.getScoreboard().updatePlayerTeam(otherNexusPlayer);
                                        } else {
                                            nexusPlayer.getScoreboard().refreshPlayerTeam(otherNexusPlayer);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 1L, 20L);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    NexusPlayer nexusPlayer = playerManager.getNexusPlayer(player.getUniqueId());
                    if (nexusPlayer != null) {
                        if (nexusPlayer.getScoreboard() != null) {
                            nexusPlayer.getScoreboard().update();
                        }
                    }
                }
            }
        }.runTaskTimer(this, 1L, 1L);
    }
    
    @Override
    public void onDisable() {
        playerManager.saveData();
    }
    
    private void registerCommand(String cmd, TabExecutor tabExecutor) {
        PluginCommand command = getCommand(cmd);
        command.setExecutor(tabExecutor);
        command.setTabCompleter(tabExecutor);
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
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
    
    public TagManager getTagManager() {
        return this.tagManager;
    }
}