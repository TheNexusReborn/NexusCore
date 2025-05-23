package com.thenexusreborn.api;

import com.stardevllc.clock.ClockManager;
import com.stardevllc.observable.collections.ObservableHashSet;
import com.stardevllc.observable.collections.ObservableSet;
import com.stardevllc.registry.StringRegistry;
import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.experience.LevelManager;
import com.thenexusreborn.api.experience.PlayerExperience;
import com.thenexusreborn.api.gamearchive.*;
import com.thenexusreborn.api.nickname.NickPerms;
import com.thenexusreborn.api.nickname.Nickname;
import com.thenexusreborn.api.nickname.list.*;
import com.thenexusreborn.api.nickname.player.*;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.player.PlayerManager.Name;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentManager;
import com.thenexusreborn.api.registry.ToggleRegistry;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.api.server.ServerRegistry;
import com.thenexusreborn.api.sql.DatabaseRegistry;
import com.thenexusreborn.api.sql.objects.Row;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.api.util.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NexusReborn {
    private static NexusReborn instance;
    public static final NetworkType NETWORK_TYPE = NetworkType.SINGLE;
    
    public static void setInstance(NexusReborn api) {
        instance = api;
    }
    
    protected final Logger logger;
    protected final File folder;
    protected final PlayerManager playerManager;
    protected final Environment environment;
    protected final PunishmentManager punishmentManager;
    protected final LevelManager levelManager;
    protected ClockManager clockManager;
    protected String version;
    
    protected ServerRegistry<NexusServer> serverRegistry;
    protected ToggleRegistry toggleRegistry;
    protected StringRegistry<String> tagRegistry;
    protected DatabaseRegistry databaseRegistry;
    
    protected SQLDatabase primaryDatabase;
    protected GameLogManager gameLogManager;
    
    protected final ObservableSet<String> nicknameBlacklist = new ObservableHashSet<>();
    protected final ObservableSet<String> randomNames = new ObservableHashSet<>();
    protected final ObservableSet<String> randomSkins = new ObservableHashSet<>();
    
    protected NickPerms nickPerms;
    
    public NexusReborn(Environment environment, Logger logger, File folder, PlayerManager playerManager) {
        this.logger = logger;
        this.folder = folder;
        this.environment = environment;
        this.playerManager = playerManager;
        this.punishmentManager = new PunishmentManager();
        this.levelManager = new LevelManager();
        this.levelManager.init();
        
        URL url = NexusReborn.class.getClassLoader().getResource("nexusapi-version.txt");
        try (InputStream in = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                logger.warning("Could not find the NexusAPI Version.");
            } else {
                this.version = line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public static void init() throws Exception {
        getLogger().info("Loading NexusAPI Version v" + instance.version);
        
        instance.serverRegistry = new ServerRegistry<>();
        instance.databaseRegistry = new DatabaseRegistry(instance.logger);
        
        instance.registerDatabases(instance.databaseRegistry);
        getLogger().info("Registered the databases");
        
        for (SQLDatabase database : instance.databaseRegistry.getObjects().values()) {
            if (database.getName().toLowerCase().contains("nexus")) {
                database.registerClass(PlayerExperience.class);
                database.registerClass(PlayerTime.class);
                database.registerClass(PlayerBalance.class);
                database.registerClass(NickExperience.class);
                database.registerClass(NickBalance.class);
                database.registerClass(NickTime.class);
                database.registerClass(Nickname.class);
                database.registerClass(IPEntry.class);
                database.registerClass(Toggle.class);
                database.registerClass(NexusPlayer.class);
                database.registerClass(GameInfo.class);
                database.registerClass(GameAction.class);
                database.registerClass(Punishment.class);
                database.registerClass(Tag.class);
                database.registerClass(Session.class);
                database.registerClass(NameBlacklistEntry.class);
                database.registerClass(RandomNameEntry.class);
                database.registerClass(RandomSkinEntry.class);
                database.registerClass(NickPerms.class);
                instance.primaryDatabase = database;
            }
        }
        
        if (instance.primaryDatabase == null) {
            throw new SQLException("Could not find the primary database.");
        }
        
        instance.databaseRegistry.setup();
        getLogger().info("Successfully setup the database tables");
        
        List<NameBlacklistEntry> nicknameBlacklistEntries = instance.primaryDatabase.get(NameBlacklistEntry.class);
        for (NameBlacklistEntry entry : nicknameBlacklistEntries) {
            instance.nicknameBlacklist.add(entry.getName());
        }
        
        instance.nicknameBlacklist.addListener(e -> {
            if (e.added() != null) {
                getPrimaryDatabase().saveSilent(new NameBlacklistEntry((String) e.added()));
            } else if (e.removed() != null) {
                getPrimaryDatabase().deleteSilent(NameBlacklistEntry.class, e.removed());
            }
        });
        
        List<RandomNameEntry> randomNameEntries = instance.primaryDatabase.get(RandomNameEntry.class);
        for (RandomNameEntry entry : randomNameEntries) {
            instance.randomNames.add(entry.getName());
        }
        
        instance.randomNames.addListener(e -> {
            if (e.added() != null) {
                getPrimaryDatabase().saveSilent(new RandomNameEntry((String) e.added()));
            } else if (e.removed() != null) {
                getPrimaryDatabase().deleteSilent(RandomNameEntry.class, e.removed());
            }
        });
        
        List<RandomSkinEntry> randomSkinEntries = instance.primaryDatabase.get(RandomSkinEntry.class);
        for (RandomSkinEntry entry : randomSkinEntries) {
            instance.randomSkins.add(entry.getName());
        }
        
        instance.randomSkins.addListener(e -> {
            if (e.added() != null) {
                getPrimaryDatabase().saveSilent(new RandomSkinEntry((String) e.added()));
            } else if (e.removed() != null) {
                getPrimaryDatabase().deleteSilent(RandomSkinEntry.class, e.removed());
            }
        });
        
        try {
            instance.nickPerms = getPrimaryDatabase().get(NickPerms.class).getFirst();
        } catch (Throwable t) {
            instance.nickPerms = new NickPerms();
            getPrimaryDatabase().saveSilent(instance.nickPerms);
        }
        
        instance.toggleRegistry = new ToggleRegistry();
        
        instance.toggleRegistry.register("vanish", Rank.HELPER, "Vanish", "A staff only thing where you can be completely invisible", false);
        instance.toggleRegistry.register("incognito", Rank.MEDIA, "Incognito", "A media+ thing where you can be hidden from others", false);
        instance.toggleRegistry.register("fly", Rank.DIAMOND, "Fly", "A donor perk that allows you to fly in hubs and lobbies", false);
        instance.toggleRegistry.register("debug", Rank.ADMIN, "Debug", "A toggle that allows debugging of things", false);
        
        int initialToggleSize = instance.toggleRegistry.getObjects().size();
        getLogger().info("Registered " + initialToggleSize + " default toggle types.");
        
        instance.registerToggles(instance.toggleRegistry);
        getLogger().info("Registered " + (instance.toggleRegistry.getObjects().size() - initialToggleSize) + " additional default toggle types.");
        
        getLogger().info("Registering and Setting up Tags");
        instance.tagRegistry = new StringRegistry<>();
        String[] defaultTags = {"thicc", "son", "e-girl", "god", "e-dater", "lord", "epic", "bacca", "benja", "milk man", "champion"};
        for (String dt : defaultTags) {
            instance.tagRegistry.register(dt, dt);
        }
        getLogger().info("Registered " + instance.tagRegistry.getObjects().size() + " default tags.");
        
        for (Punishment punishment : getPrimaryDatabase().get(Punishment.class)) {
            instance.punishmentManager.addPunishment(punishment);
        }
        getLogger().info("Cached punishments in memory");
        
        instance.playerManager.getIpHistory().addAll(getPrimaryDatabase().get(IPEntry.class));
        getLogger().info("Loaded IP History");
        
        SQLDatabase database = getPrimaryDatabase();
        List<Row> playerRows = database.executeQuery("select * from players;");
        
        for (Row row : playerRows) {
            UUID uniqueId = (UUID) row.getObject("uniqueid");
            String name = row.getString("name");
            PlayerRanks playerRanks = (PlayerRanks) row.getObject("ranks");
            playerRanks.setUniqueId(uniqueId);
            instance.playerManager.getUuidNameMap().put(uniqueId, new Name(name));
            instance.playerManager.getUuidRankMap().put(uniqueId, playerRanks);
        }
        getLogger().info("Loaded basic player data (database IDs, Unique IDs and Names) - " + instance.playerManager.getUuidNameMap().size() + " total profiles.");
        
        getLogger().info("NexusAPI v" + instance.version + " load complete.");
    }
    
    public abstract void registerDatabases(DatabaseRegistry registry);
    
    public abstract void registerToggles(ToggleRegistry registry);
    
    public static ObservableSet<String> getNicknameBlacklist() {
        return instance.nicknameBlacklist;
    }
    
    public static ObservableSet<String> getRandomNames() {
        return instance.randomNames;
    }
    
    public static ObservableSet<String> getRandomSkins() {
        return instance.randomSkins;
    }
    
    public static String getVersion() {
        return instance.version;
    }
    
    public static File getFolder() {
        return instance.folder;
    }
    
    public static PlayerManager getPlayerManager() {
        return instance.playerManager;
    }
    
    public static Logger getLogger() {
        return instance.logger;
    }
    
    public static PunishmentManager getPunishmentManager() {
        return instance.punishmentManager;
    }
    
    public static GameLogManager getGameLogManager() {
        return instance.gameLogManager;
    }
    
    public static void setGameLogManager(GameLogManager gameLogManager) {
        instance.gameLogManager = gameLogManager;
    }
    
    public static void logMessage(Level level, String mainMessage, String... debug) {
        Logger logger = NexusReborn.getLogger();
        logger.log(level, "----------- Nexus Log -----------");
        logger.log(level, mainMessage);
        if (debug != null) {
            for (String s : debug) {
                logger.log(level, s);
            }
        }
        logger.log(level, "---------------------------------");
    }
    
    public static SQLDatabase getPrimaryDatabase() {
        return instance.primaryDatabase;
    }
    
    public static ToggleRegistry getToggleRegistry() {
        return instance.toggleRegistry;
    }
    
    public static URLClassLoader getLoader() {
        ClassLoader classLoader = instance.getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            return (URLClassLoader) classLoader;
        }
        return null;
    }
    
    public static LevelManager getLevelManager() {
        return instance.levelManager;
    }
    
    public static ClockManager getClockManager() {
        return instance.clockManager;
    }
    
    public static void setClockManager(ClockManager clockManager) {
        instance.clockManager = clockManager;
    }
    
    public static ServerRegistry<NexusServer> getServerRegistry() {
        return instance.serverRegistry;
    }
    
    public static NickPerms getNickPerms() {
        return instance.nickPerms;
    }
    
    public static void sendDebugMessage(String message) {
        for (NexusPlayer player : getPlayerManager().getPlayers().values()) {
            if (!player.isOnline()) {
                continue;
            }
            
            if (!player.getToggleValue("debug")) {
                continue;
            }
            
            player.sendMessage("&6&l[DEBUG] &7&o" + message);
        }
    }
    
    public static void sendDebugMessage(CommandSender sender, String message) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(StarColors.color("&6&l[DEBUG] &7&o" + message));
        } else {
            NexusPlayer player = getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
            if (player.getToggleValue("debug")) {
                sender.sendMessage(StarColors.color("&6&l[DEBUG] &7&o" + message));
            }
        }
    }
}
