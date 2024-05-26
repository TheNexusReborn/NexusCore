package com.thenexusreborn.nexuscore;

import com.stardevllc.starchat.StarChat;
import com.stardevllc.starchat.channels.ChatChannel;
import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starcore.utils.ServerProperties;
import com.stardevllc.starlib.clock.ClockManager;
import com.stardevllc.starlib.helper.FileHelper;
import com.sun.net.httpserver.HttpServer;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameLogExporter;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.api.server.VirtualServer;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.api.events.NexusServerSetupEvent;
import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.chat.PunishmentChannel;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.hooks.NexusPapiExpansion;
import com.thenexusreborn.nexuscore.http.GameHttpHandler;
import com.thenexusreborn.nexuscore.http.ServerHttpHandler;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.server.CoreInstanceServer;
import com.thenexusreborn.nexuscore.thread.*;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

public class NexusCore extends JavaPlugin implements Listener {

    private final List<NexusSpigotPlugin> nexusPlugins = new ArrayList<>();
    private ChatManager chatManager;
    private ToggleCmds toggleCmdExecutor;
    private Supplier<String> motdSupplier;

    private StarChat starChatPlugin;
    private PunishmentChannel punishmentChannel;

    private InstanceServer nexusServer;
    private ClockManager clockManager;
    private GameLogExporter gameLogExporter;

    @Override
    public void onEnable() {
        getLogger().info("Loading NexusCore v" + getDescription().getVersion());
        this.saveDefaultConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("StarCore") == null) {
            getLogger().severe("StarCore not found, disabling NexusCore.");
            pluginManager.disablePlugin(this);
            return;
        }

        this.clockManager = Bukkit.getServicesManager().getRegistration(ClockManager.class).getProvider();

        if (pluginManager.getPlugin("StarChat") == null) {
            getLogger().severe("StarChat not found, disabling NexusCore.");
            pluginManager.disablePlugin(this);
            return;
        }

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

        new NexusPapiExpansion(this).register();
        getLogger().info("Hooked into PlaceholderAPI");

        this.starChatPlugin = (StarChat) pluginManager.getPlugin("StarChat");
        this.starChatPlugin.getGlobalChannel().setSenderFormat("&8(&2&l%nexuscore_level%&8) &r%nexuscore_displayname%&8: %nexuscore_chatcolor%{message}");
        this.starChatPlugin.getStaffChannel().setSenderFormat("&2&l[&aSTAFF&2&l] &r%nexuscore_coloredname%: &f{message}");
        this.starChatPlugin.getStaffChannel().setSystemFormat("&2&l[&aSTAFF&2&l] &f{message}");
        this.starChatPlugin.getStaffChannel().setSendPermission("nexuscore.staff.send");
        this.starChatPlugin.getStaffChannel().setViewPermission("nexuscore.staff.view");
        this.punishmentChannel = new PunishmentChannel(this);
        getLogger().info("Hooked into StarChat");

        chatManager = new ChatManager(this);
        getLogger().info("Registered Chat Manager");

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getLogger().info("Registered BungeeCore Plugin Channel");

        Bukkit.getServer().getPluginManager().registerEvents((SpigotPlayerManager) NexusAPI.getApi().getPlayerManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(chatManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Registered Event Listeners");

        registerCommand("rank", new RankCommand(this));
        getCommand("tag").setExecutor(new TagCommand(this));
        getCommand("say").setExecutor(new SayCommand(this));
        getCommand("message").setExecutor(new MessageCommand());
        getCommand("reply").setExecutor(new ReplyCommand());
        getCommand("me").setExecutor(new MeCommand());
        getCommand("list").setExecutor(new ListCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("servers").setExecutor(new ServersCommand(this));
        getCommand("discord").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(ColorHandler.getInstance().color(MsgType.INFO + "Discord: &bhttps://discord.gg/bawZKSWEpT"));
            return true;
        });

        getCommand("shop").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(ColorHandler.getInstance().color(MsgType.INFO + "Shop: &bhttps://nexusreborn.tebex.io/"));
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
        new PlayerVisibilityThread(this).start();
        getLogger().info("Registered Tasks");

        getServer().getScheduler().runTaskLater(this, () -> {
            NexusServerSetupEvent event = new NexusServerSetupEvent(NexusAPI.NETWORK_TYPE);
            getServer().getPluginManager().callEvent(event);
            nexusServer = event.getServer();
            if (nexusServer == null) {
                CoreInstanceServer coreInstanceServer = new CoreInstanceServer(this);
                coreInstanceServer.setPrimaryVirtualServer(event.getPrimaryVirtualServer());
                Map<String, VirtualServer> virtualServers = event.getVirtualServers();
                for (VirtualServer server : virtualServers.values()) {
                    coreInstanceServer.getChildServers().register(server);
                    server.setParentServer(coreInstanceServer);
                    NexusAPI.getApi().getServerRegistry().register(server);
                }

                coreInstanceServer.onStart();
                coreInstanceServer.getChildServers().forEach(NexusServer::onStart);
                nexusServer = coreInstanceServer;
            }
            NexusAPI.getApi().getServerRegistry().register(nexusServer);
        }, 1L);
        
        NexusAPI.getApi().setGameLogExporter(new GameLogExporter(new File(getDataFolder(), "export" + File.separator + "games")));
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                NexusAPI.getApi().getGameLogExporter().exportGames();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
                HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8051), 0);
                server.createContext("/server", new ServerHttpHandler(this));
                server.createContext("/game", new GameHttpHandler(this));
                server.setExecutor(threadPoolExecutor);
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void addNexusPlugin(NexusSpigotPlugin plugin) {
        this.nexusPlugins.add(plugin);
    }

    @Override
    public void onDisable() {
        NexusAPI.getApi().getPlayerManager().saveData();

        String levelName = ServerProperties.getLevelName();
        File worldFolder = new File("./", levelName);
        File playerdataFolder = new File(worldFolder, "playerdata");
        if (playerdataFolder.exists() && playerdataFolder.isDirectory()) {
            FileHelper.deleteDirectory(playerdataFolder.toPath());
        }
    }
    
    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        e.setMotd(ColorHandler.getInstance().color("            &5&lTHE NEXUS REBORN &e&lALPHA\n              &7Minecraft Version 1.8"));

        Iterator<Player> iterator = e.iterator();
        while (iterator.hasNext()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(iterator.next().getUniqueId());
            if (nexusPlayer.getToggleValue("vanish") || nexusPlayer.getToggleValue("incognito")) {
                iterator.remove();
            }
        }
    }

    private void registerCommand(String cmd, TabExecutor tabExecutor) {
        PluginCommand command = getCommand(cmd);
        command.setExecutor(tabExecutor);
        command.setTabCompleter(tabExecutor);
    }

    public ChatManager getChatManager() {
        return chatManager;
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

    public StarChat getStarChatPlugin() {
        return starChatPlugin;
    }

    public ChatChannel getGlobalChannel() {
        return starChatPlugin.getGlobalChannel();
    }

    public ChatChannel getStaffChannel() {
        return starChatPlugin.getStaffChannel();
    }

    public PunishmentChannel getPunishmentChannel() {
        return punishmentChannel;
    }

    public InstanceServer getNexusServer() {
        return nexusServer;
    }

    public ClockManager getClockManager() {
        return this.clockManager;
    }
    
    /* ServerPingEvent
    Iterator<Player> iterator = e.iterator();
        while(iterator.hasNext()) {
            Player player = iterator.next();
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer == null) {
                continue;
            }
            if (nexusPlayer.getToggleValue("vanish") || nexusPlayer.getToggleValue("incognito")) {
                iterator.remove();
            }
        }
     */
    
    /*
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        SimpleCommandMap commandMap = craftServer.getCommandMap();
        commandMap.register(plugin.getName(), command);
     */
}