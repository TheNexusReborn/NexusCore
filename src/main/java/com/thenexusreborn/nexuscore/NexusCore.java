package com.thenexusreborn.nexuscore;

import com.stardevllc.starclock.ClockManager;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.server.SpigotServerManager;
import com.thenexusreborn.nexuscore.thread.*;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.nms.NMS;
import com.thenexusreborn.nexuscore.util.nms.NMS.Version;
import me.firestar311.starsql.api.objects.SQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NexusCore extends JavaPlugin {
    
    private NMS nms;
    private final List<NexusSpigotPlugin> nexusPlugins = new ArrayList<>();
    private ChatManager chatManager;
    private ToggleCmds toggleCmdExecutor;
    private Supplier<String> motdSupplier;

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
        
        NexusAPI.getApi().setClockManager(getServer().getServicesManager().getRegistration(ClockManager.class).getProvider());
        
        Bukkit.getServicesManager().register(SQLDatabase.class, NexusAPI.getApi().getPrimaryDatabase(), this, ServicePriority.Highest);
        
        nms = NMS.getNMS(Version.MC_1_8_R3);
        getLogger().info("Registered NMS Version");
        
        chatManager = new ChatManager(this);
        getLogger().info("Registered Chat Manager");
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getLogger().info("Registered BungeeCore Plugin Channel");
        
        Bukkit.getServer().getPluginManager().registerEvents((SpigotPlayerManager) NexusAPI.getApi().getPlayerManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(chatManager, this);
        Bukkit.getServer().getPluginManager().registerEvents((SpigotServerManager) NexusAPI.getApi().getServerManager(), this);
        getLogger().info("Registered Event Listeners");
        
        registerCommand("rank", new RankCommand(this));
        registerCommand("setstat", new SetStatCmd(this));
        getCommand("tag").setExecutor(new TagCommand(this));
        getCommand("say").setExecutor(new SayCommand(this));
        getCommand("message").setExecutor(new MessageCommand());
        getCommand("reply").setExecutor(new ReplyCommand());
        getCommand("me").setExecutor(new MeCommand());
        getCommand("list").setExecutor(new ListCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("discord").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(MCUtils.color(MsgType.INFO + "Discord: &bhttps://discord.gg/bawZKSWEpT"));
            return true;
        });
        
        getCommand("shop").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(MCUtils.color(MsgType.INFO + "Shop: &bhttps://nexusreborn.tebex.io/"));
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
    
        toggleCmdExecutor = new ToggleCmds();
        getCommand("incognito").setExecutor(toggleCmdExecutor);
        getCommand("vanish").setExecutor(toggleCmdExecutor);
        getCommand("fly").setExecutor(toggleCmdExecutor);
        
        getCommand("nexusversion").setExecutor(new NexusVersionCmd(this));
        getCommand("tps").setExecutor(new PerformanceCmd(this));
        getCommand("playtime").setExecutor(new PlaytimeCommand(this));
        
        getLogger().info("Registered Commands");
        
        new PlayerHUDThread(this).start();
        new PlayerTablistThread(this).start();
        new PlayerPermThread(this).start();
        new ServerUpdateThread(this).start();
        new PlayerLoadActionBarThread(this).start();
        getLogger().info("Registered Tasks");
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
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(serverInfo);
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
    
    public ToggleCmds getToggleCmdExecutor() {
        return toggleCmdExecutor;
    }

    public Supplier<String> getMotdSupplier() {
        return motdSupplier;
    }

    public void setMotdSupplier(Supplier<String> motdSupplier) {
        this.motdSupplier = motdSupplier;
    }
}