package com.thenexusreborn.nexuscore;

import com.stardevllc.clock.ClockManager;
import com.stardevllc.helper.FileHelper;
import com.stardevllc.starchat.StarChat;
import com.stardevllc.starchat.channels.ChatChannel;
import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.utils.ServerProperties;
import com.stardevllc.starui.GuiManager;
import com.sun.net.httpserver.HttpServer;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.gamearchive.GameLogExporter;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.*;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.api.events.NexusServerSetupEvent;
import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.chat.PunishmentChannel;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.cmds.bot.BotCommand;
import com.thenexusreborn.nexuscore.cmds.rank.RankCommand;
import com.thenexusreborn.nexuscore.cmds.servers.ServersCommand;
import com.thenexusreborn.nexuscore.cmds.tag.TagCommand;
import com.thenexusreborn.nexuscore.cmds.tag.admin.TagAdminCommand;
import com.thenexusreborn.nexuscore.discord.DiscordVerifyCode;
import com.thenexusreborn.nexuscore.discord.NexusBot;
import com.thenexusreborn.nexuscore.hooks.NexusPapiExpansion;
import com.thenexusreborn.nexuscore.http.GameHttpHandler;
import com.thenexusreborn.nexuscore.http.ServerHttpHandler;
import com.thenexusreborn.nexuscore.nickname.NickWrapper_v1_8_R3;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.server.CoreInstanceServer;
import com.thenexusreborn.nexuscore.thread.*;
import com.thenexusreborn.nexuscore.util.MsgType;
import net.dv8tion.jda.internal.utils.JDALogger;
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
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

public class NexusCore extends JavaPlugin implements Listener {

    private final List<NexusSpigotPlugin> nexusPlugins = new ArrayList<>();
    private ChatManager chatManager;
    private Supplier<String> motdSupplier;

    private StarChat starChatPlugin;
    private PunishmentChannel punishmentChannel;

    private InstanceServer nexusServer;
    private ClockManager clockManager;
    private GuiManager guiManager;

    private NexusBot nexusBot;
    
    private NickWrapper_v1_8_R3 nickWrapper = new NickWrapper_v1_8_R3();

    private List<DiscordVerifyCode> discordVerifyCodes = new ArrayList<>();

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

        this.clockManager = new ClockManager(getLogger(), 50);
        getServer().getScheduler().runTaskTimer(this, this.clockManager.getRunnable(), 1L, 1L);
        Bukkit.getServicesManager().register(ClockManager.class, this.clockManager, this, ServicePriority.High);

        guiManager = new GuiManager(this);
        guiManager.setup();
        Bukkit.getServicesManager().register(GuiManager.class, this.guiManager, this, ServicePriority.High);

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
        this.starChatPlugin.getStaffChannel().setSenderFormat("&2&l[&aSTAFF&2&l] &r%nexuscore_coloredname_true%: &f{message}");
        this.starChatPlugin.getStaffChannel().setSystemFormat("&2&l[&aSTAFF&2&l] &f{message}");
        this.starChatPlugin.getStaffChannel().setSendPermission("nexuscore.staff.send");
        this.starChatPlugin.getStaffChannel().setViewPermission("nexuscore.staff.view");
        this.punishmentChannel = new PunishmentChannel(this);
        getLogger().info("Hooked into StarChat");

        chatManager = new ChatManager(this);
        getLogger().info("Registered Chat Manager");
        
        JDALogger.setFallbackLoggerEnabled(false);
        this.nexusBot = new NexusBot(this);

        Bukkit.getServer().getPluginManager().registerEvents((SpigotPlayerManager) NexusAPI.getApi().getPlayerManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(chatManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Registered Event Listeners");
        
        new NickCommand(this);
        new UnnickCommand(this);
        new RealnameCommand(this);
        new BotCommand(this);
        new ProfileCmd(this);

        new RankCommand(this);
        new TagCommand(this);
        new TagAdminCommand(this);
        new SayCommand(this);
        new MessageCommand(this);
        new ReplyCommand(this);
        new ListCommand(this);
        new BalanceCommand(this);
        new ServersCommand(this);
        
        getCommand("discord").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(StarColors.color(MsgType.INFO + "Discord: &bhttps://discord.gg/bawZKSWEpT"));
            return true;
        });

        getCommand("shop").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(StarColors.color(MsgType.INFO + "Shop: &bhttps://nexusreborn.tebex.io/"));
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

        new AltsCommand(this);

        new ToggleCmd(this, "incognito", "i");
        new ToggleCmd(this, "vanish", "v");
        new ToggleCmd(this, "fly");

        new NexusVersionCmd(this);
        new PerformanceCmd(this);
        new PlaytimeCommand(this);
        new VerifyCommand(this);

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
        
        this.nexusBot.start();
    }
    
    public NickWrapper_v1_8_R3 getNickWrapper() {
        return nickWrapper;
    }
    
    public List<DiscordVerifyCode> getDiscordVerifyCodes() {
        return discordVerifyCodes;
    }

    public void addNexusPlugin(NexusSpigotPlugin plugin) {
        this.nexusPlugins.add(plugin);
    }

    public NexusBot getNexusBot() {
        return nexusBot;
    }

    @Override
    public void onDisable() {
        NexusAPI.getApi().getPlayerManager().saveData();

        if (this.nexusBot != null) {
            this.nexusBot.shutdown();
        }

        String levelName = ServerProperties.getLevelName();
        File worldFolder = new File("./", levelName);
        File playerdataFolder = new File(worldFolder, "playerdata");
        if (playerdataFolder.exists() && playerdataFolder.isDirectory()) {
            FileHelper.deleteDirectory(playerdataFolder.toPath());
        }
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        e.setMotd(StarColors.color("            &5&lTHE NEXUS REBORN &e&lALPHA\n       &7Minecraft Version 1.8-1.21.4"));

        Iterator<Player> iterator = e.iterator();
        while (iterator.hasNext()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(iterator.next().getUniqueId());
            if (nexusPlayer == null) {
                continue;
            }
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