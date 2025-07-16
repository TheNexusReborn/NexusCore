package com.thenexusreborn.nexuscore;

import com.stardevllc.clock.ClockManager;
import com.stardevllc.helper.FileHelper;
import com.stardevllc.starchat.StarChat;
import com.stardevllc.starchat.channels.ChatChannel;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.utils.ServerProperties;
import com.stardevllc.starui.GuiManager;
import com.sun.net.httpserver.HttpServer;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.gamearchive.GameInfo;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.server.*;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.api.events.NexusServerSetupEvent;
import com.thenexusreborn.nexuscore.chat.ChatManager;
import com.thenexusreborn.nexuscore.chat.PunishmentChannel;
import com.thenexusreborn.nexuscore.cmds.*;
import com.thenexusreborn.nexuscore.cmds.bot.BotCommand;
import com.thenexusreborn.nexuscore.cmds.nickadmin.NickAdminCommand;
import com.thenexusreborn.nexuscore.cmds.rank.RankCommand;
import com.thenexusreborn.nexuscore.cmds.servers.ServersCommand;
import com.thenexusreborn.nexuscore.cmds.tag.TagCommand;
import com.thenexusreborn.nexuscore.cmds.tag.admin.TagAdminCommand;
import com.thenexusreborn.nexuscore.discord.DiscordVerifyCode;
import com.thenexusreborn.nexuscore.discord.NexusBot;
import com.thenexusreborn.nexuscore.hooks.NexusPapiExpansion;
import com.thenexusreborn.nexuscore.http.*;
import com.thenexusreborn.nexuscore.nickname.NickWrapper_v1_8_R3;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.server.CoreInstanceServer;
import com.thenexusreborn.nexuscore.thread.*;
import com.thenexusreborn.nexuscore.util.MsgType;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.node.Node;
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

import java.io.*;
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
    
    private NickWrapper_v1_8_R3 nickWrapper = new NickWrapper_v1_8_R3();

    private NexusBot nexusBot;
    
    private List<DiscordVerifyCode> discordVerifyCodes = new ArrayList<>();
     
    private LuckPerms luckPerms;
    
    @Override
    public void onEnable() {
        getLogger().info("Loading NexusCore v" + getDescription().getVersion());
        this.saveDefaultConfig();
        
        if (!getConfig().contains("motd")) {
            getConfig().set("motd", List.of("&d&lThe Nexus Reborn &7&l-> &e&lALPHA &7[1.8-1.21.5]", "&7Discord: &3thenexusreborn.com/discord"));
            saveConfig();
        }

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

        NexusReborn.setInstance(new SpigotNexusAPI(this));
        try {
            NexusReborn.init();
        } catch (Exception e) {
            getLogger().severe("Error while enabling the NexusAPI. Disabling plugin");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        NexusReborn.setClockManager(this.clockManager);

        Bukkit.getServicesManager().register(SQLDatabase.class, NexusReborn.getPrimaryDatabase(), this, ServicePriority.Highest);

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

        Bukkit.getServer().getPluginManager().registerEvents((SpigotPlayerManager) NexusReborn.getPlayerManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(chatManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Registered Event Listeners");
        
        this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        GroupManager groupManager = this.luckPerms.getGroupManager();
        for (int i = Rank.values().length - 1; i >= 0; i--) {
            Rank rank = Rank.values()[i];
            Group group = groupManager.getGroup(rank.name().toLowerCase());
            if (group == null) {
                group = groupManager.createAndLoadGroup(rank.name().toLowerCase()).join();
            }
            
            group.data().clear();
            
            Group parent;
            if (i == Rank.values().length - 1) {
                parent = groupManager.getGroup("default");
            } else {
                parent = groupManager.getGroup(Rank.values()[i+1].name().toLowerCase());
            }
            
            group.data().add(Node.builder("group." + parent.getName()).build());
            
            for (String permission : rank.getPermissions()) {
                Node node = Node.builder(permission).build();
                if (!group.data().contains(node, Node::equals).asBoolean()) {
                    group.data().add(node);
                }
            }
            
            for (String negatedPermission : rank.getNegatedPermissions()) {
                Node node = Node.builder(negatedPermission).value(false).build();
                if (!group.data().contains(node, Node::equals).asBoolean()) {
                    group.data().add(node);
                }
            }
            
            groupManager.saveGroup(group);
        }
        
        new NickCommand(this);
        new UnnickCommand(this);
        new RealnameCommand(this);
        new BotCommand(this);
        new ProfileCmd(this);
        new NickAdminCommand(this);

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
        new PingCommand(this);

        new ToggleCmd(this, "incognito", "i");
        new ToggleCmd(this, "vanish", "v");
        new ToggleCmd(this, "fly");
        new ToggleCmd(this, "debug");

        new NexusVersionCmd(this);
        new PerformanceCmd(this);
        new PlaytimeCommand(this);
        new VerifyCommand(this);
        
        new ToggleOPCmd(this);

        getLogger().info("Registered Commands");

        new PlayerHUDThread(this).start();
        new ServerUpdateThread(this).start();
        new PlayerVisibilityThread(this).start();
        getLogger().info("Registered Tasks");

        getServer().getScheduler().runTaskLater(this, () -> {
            NexusServerSetupEvent event = new NexusServerSetupEvent(NexusReborn.NETWORK_TYPE);
            getServer().getPluginManager().callEvent(event);
            nexusServer = event.getServer();
            if (nexusServer == null) {
                CoreInstanceServer coreInstanceServer = new CoreInstanceServer(this);
                coreInstanceServer.setPrimaryVirtualServer(event.getPrimaryVirtualServer());
                Map<String, VirtualServer> virtualServers = event.getVirtualServers();
                for (VirtualServer server : virtualServers.values()) {
                    coreInstanceServer.getChildServers().register(server);
                    server.setParentServer(coreInstanceServer);
                    NexusReborn.getServerRegistry().register(server);
                }

                coreInstanceServer.onStart();
                coreInstanceServer.getChildServers().forEach(NexusServer::onStart);
                nexusServer = coreInstanceServer;
            }
            NexusReborn.getServerRegistry().register(nexusServer);
        }, 1L);

        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                NexusReborn.getGameLogManager().exportGames();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
                HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8051), 0);
                server.createContext("/", new RootHttpHandler(this));
                server.createContext("/server", new ServerHttpHandler(this));
                server.createContext("/game", new GameHttpHandler(this));
                server.setExecutor(threadPoolExecutor);
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        File playersWithNoUUIDFile = new File(getDataFolder() + File.separator + "playerswithnouuid.txt");
        if (!playersWithNoUUIDFile.exists()) {
            try {
                playersWithNoUUIDFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try (FileWriter fileWriter = new FileWriter(playersWithNoUUIDFile)) {
            for (String pnu : GameInfo.playersWithNoUUID) {
                fileWriter.write(pnu + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.nexusBot.start();
        for (NexusSpigotPlugin nexusPlugin : this.getNexusPlugins()) {
            nexusPlugin.registerChannels(this.nexusBot);
        }
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
    
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
    
    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
            Session session = nexusPlayer.getSession();
            session.end();
            nexusPlayer.setLastLogout(session.getEnd());
            long playTime = session.getTimeOnline();
            NexusReborn.getPrimaryDatabase().deleteSilent(session);
            nexusPlayer.getPlayerTime().addPlaytime(playTime);
            NexusReborn.getPrimaryDatabase().saveSilent(nexusPlayer);
        }
        
        NexusReborn.getPlayerManager().saveData();

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
        List<String> motd = this.getConfig().getStringList("motd");
        StringBuilder motdBuilder = new StringBuilder();
        for (String line : motd) {
            motdBuilder.append(line).append("\n");
        }
        
        motdBuilder.delete(motdBuilder.length() - 1, motdBuilder.length());
        
        e.setMotd(StarColors.color(motdBuilder.toString()));

        Iterator<Player> iterator = e.iterator();
        while (iterator.hasNext()) {
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(iterator.next().getUniqueId());
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