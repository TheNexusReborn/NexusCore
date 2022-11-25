package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.anticheat.AnticheatManager;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.menu.MenuManager;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.task.*;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.nms.NMS;
import com.thenexusreborn.nexuscore.util.nms.NMS.Version;
import com.thenexusreborn.nexuscore.util.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NexusCore extends JavaPlugin {
    
    private NMS nms;
    
    private final List<NexusSpigotPlugin> nexusPlugins = new ArrayList<>();
    
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        getLogger().info("Loading NexusCore v" + getDescription().getVersion());
        this.saveDefaultConfig();
        
        NexusAPI.setApi(new SpigotNexusAPI(this));
        try {
            NexusAPI.getApi().init();
        } catch (Exception e) {
            getLogger().severe("Error while enabling the NexusAPI. Disabling plugin");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        nms = NMS.getNMS(Version.MC_1_8_R3);
        getLogger().info("Registered NMS Version");
        
        Updater updater = new Updater(this);
        Bukkit.getServer().getScheduler().runTaskTimer(this, updater, 1L, 1L);
        getLogger().info("Registered Updater Utility");
        
        chatManager = new ChatManager(this);
        getLogger().info("Registered Chat Manager");
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getLogger().info("Registered BungeeCore Plugin Channel");
        
        Bukkit.getServer().getPluginManager().registerEvents((SpigotPlayerManager) NexusAPI.getApi().getPlayerManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(chatManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(new MenuManager(), this);
        getLogger().info("Registered Event Listeners");
        
        registerCommand("rank", new RankCommand(this));
        registerCommand("setstat", new SetStatCmd(this));
        getCommand("tag").setExecutor(new TagCommand(this));
        getCommand("say").setExecutor(new SayCommand(this));
        getCommand("message").setExecutor(new MessageCommand());
        getCommand("reply").setExecutor(new ReplyCommand());
        getCommand("me").setExecutor(new MeCommand());
        getCommand("nexusadmin").setExecutor(new NexusAdminCmd(this));
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
        
        PunishRemoveCommands prCmds = new PunishRemoveCommands();
        getCommand("unban").setExecutor(prCmds);
        getCommand("unmute").setExecutor(prCmds);
        getCommand("pardon").setExecutor(prCmds);
        getCommand("unblacklist").setExecutor(prCmds);
        
        PunishmentHistoryCmds phCmds = new PunishmentHistoryCmds(this);
        getCommand("history").setExecutor(phCmds);
        getCommand("staffhistory").setExecutor(phCmds);
        
        getCommand("alts").setExecutor(new AltsCommand(this));
        
        ToggleCmds toggleCmds = new ToggleCmds();
        getCommand("incognito").setExecutor(toggleCmds);
        getCommand("vanish").setExecutor(toggleCmds);
        getCommand("fly").setExecutor(toggleCmds);
        
        getCommand("nexusversion").setExecutor(new NexusVersionCmd(this));
        getCommand("tps").setExecutor(new PerformanceCmd(this));
        
        getLogger().info("Registered Commands");
        
        new PlayerHUDTask(this).start();
        new PlayerTablistTask(this).start();
        new PlayerPermTask(this).start();
        new ServerUpdateTask(this).start();
        new ClickCheckerTask(this).start();
        new PlayerLoadActionBarTask(this).start();
        getLogger().info("Registered Tasks");
        
        if (getServer().getPluginManager().getPlugin("Spartan") != null) {
            getServer().getPluginManager().registerEvents(new AnticheatManager(), this);
            getLogger().info("Registered Anticheat Manager");
        }
    }
    
    public void addNexusPlugin(NexusSpigotPlugin plugin) {
        this.nexusPlugins.add(plugin);
    }
    
    @Override
    public void onDisable() {
        NexusAPI.getApi().getPlayerManager().saveData();
        ServerInfo serverInfo = NexusAPI.getApi().getServerManager().getCurrentServer();
        serverInfo.setStatus("offline");
        serverInfo.setState("none");
        serverInfo.setPlayers(0);
        NexusAPI.getApi().getPrimaryDatabase().push(serverInfo);
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
    
    public List<NexusSpigotPlugin> getNexusPlugins() {
        return new ArrayList<>(this.nexusPlugins);
    }
}