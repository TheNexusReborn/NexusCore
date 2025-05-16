package com.thenexusreborn.api;

import com.stardevllc.clock.ClockManager;
import com.stardevllc.observable.collections.ObservableHashSet;
import com.stardevllc.observable.collections.ObservableSet;
import com.stardevllc.registry.StringRegistry;
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

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NexusAPI {
    private static NexusAPI instance;
    public static final NetworkType NETWORK_TYPE = NetworkType.SINGLE;

    public static void setApi(NexusAPI api) {
        instance = api;
    }

    public static NexusAPI getApi() {
        return instance;
    }

    protected final Logger logger;
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

    public NexusAPI(Environment environment, Logger logger, PlayerManager playerManager) {
        this.logger = logger;
        this.environment = environment;
        this.playerManager = playerManager;
        this.punishmentManager = new PunishmentManager();
        this.levelManager = new LevelManager();
        this.levelManager.init();

        URL url = NexusAPI.class.getClassLoader().getResource("nexusapi-version.txt");
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

    public final void init() throws Exception {
        getLogger().info("Loading NexusAPI Version v" + this.version);
        
        serverRegistry = new ServerRegistry<>();
        databaseRegistry = new DatabaseRegistry(logger);

        registerDatabases(databaseRegistry);
        getLogger().info("Registered the databases");

        for (SQLDatabase database : databaseRegistry.getObjects().values()) {
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
                this.primaryDatabase = database;
            }
        }

        if (primaryDatabase == null) {
            throw new SQLException("Could not find the primary database.");
        }

        databaseRegistry.setup();
        getLogger().info("Successfully setup the database tables");
        
        List<NameBlacklistEntry> nicknameBlacklistEntries = primaryDatabase.get(NameBlacklistEntry.class);
        for (NameBlacklistEntry entry : nicknameBlacklistEntries) {
            this.nicknameBlacklist.add(entry.getName());
        }
        
        this.nicknameBlacklist.addListener(e -> {
            if (e.added() != null) {
                getPrimaryDatabase().saveSilent(new NameBlacklistEntry((String) e.added()));
            } else if (e.removed() != null) {
                getPrimaryDatabase().deleteSilent(NameBlacklistEntry.class, e.removed());
            }
        });
        
        List<RandomNameEntry> randomNameEntries = primaryDatabase.get(RandomNameEntry.class);
        for (RandomNameEntry entry : randomNameEntries) {
            this.randomNames.add(entry.getName());
        }
        
        this.randomNames.addListener(e -> {
            if (e.added() != null) {
                getPrimaryDatabase().saveSilent(new RandomNameEntry((String) e.added()));
            } else if (e.removed() != null) {
                getPrimaryDatabase().deleteSilent(RandomNameEntry.class, e.removed());
            }
        });
        
        List<RandomSkinEntry> randomSkinEntries = primaryDatabase.get(RandomSkinEntry.class);
        for (RandomSkinEntry entry : randomSkinEntries) {
            this.randomSkins.add(entry.getName());
        }
        
        this.randomSkins.addListener(e -> {
            if (e.added() != null) {
                getPrimaryDatabase().saveSilent(new RandomSkinEntry((String) e.added()));
            } else if (e.removed() != null) {
                getPrimaryDatabase().deleteSilent(RandomSkinEntry.class, e.removed());
            }
        });
        
        try {
            this.nickPerms = getPrimaryDatabase().get(NickPerms.class).getFirst();
        } catch (Throwable t) {
            this.nickPerms = new NickPerms();
            getPrimaryDatabase().saveSilent(this.nickPerms);
        }
        
        toggleRegistry = new ToggleRegistry();

        toggleRegistry.register("vanish", Rank.HELPER, "Vanish", "A staff only thing where you can be completely invisible", false);
        toggleRegistry.register("incognito", Rank.MEDIA, "Incognito", "A media+ thing where you can be hidden from others", false);
        toggleRegistry.register("fly", Rank.DIAMOND, "Fly", "A donor perk that allows you to fly in hubs and lobbies", false);
        toggleRegistry.register("debug", Rank.ADMIN, "Debug", "A toggle that allows debugging of things", false);

        int initialToggleSize = toggleRegistry.getObjects().size();
        getLogger().info("Registered " + initialToggleSize + " default toggle types.");

        registerToggles(toggleRegistry);
        getLogger().info("Registered " + (toggleRegistry.getObjects().size() - initialToggleSize) + " additional default toggle types.");

        getLogger().info("Registering and Setting up Tags");
        this.tagRegistry = new StringRegistry<>();
        String[] defaultTags = {"thicc", "son", "e-girl", "god", "e-dater", "lord", "epic", "bacca", "benja", "milk man", "champion"};
        for (String dt : defaultTags) {
            this.tagRegistry.register(dt, dt);
        }
        getLogger().info("Registered " + this.tagRegistry.getObjects().size() + " default tags.");

        for (Punishment punishment : getPrimaryDatabase().get(Punishment.class)) {
            punishmentManager.addPunishment(punishment);
        }
        getLogger().info("Cached punishments in memory");

        playerManager.getIpHistory().addAll(getPrimaryDatabase().get(IPEntry.class));
        getLogger().info("Loaded IP History");

        SQLDatabase database = getPrimaryDatabase();
        List<Row> playerRows = database.executeQuery("select * from players;");

        for (Row row : playerRows) {
            UUID uniqueId = (UUID) row.getObject("uniqueid");
            String name = row.getString("name");
            PlayerRanks playerRanks = (PlayerRanks) row.getObject("ranks");
            playerRanks.setUniqueId(uniqueId);
            playerManager.getUuidNameMap().put(uniqueId, new Name(name));
            playerManager.getUuidRankMap().put(uniqueId, playerRanks);
        }
        getLogger().info("Loaded basic player data (database IDs, Unique IDs and Names) - " + playerManager.getUuidNameMap().size() + " total profiles.");
        
        getLogger().info("NexusAPI v" + this.version + " load complete.");
    }

    public abstract void registerDatabases(DatabaseRegistry registry);

    public abstract void registerToggles(ToggleRegistry registry);
    
    public ObservableSet<String> getNicknameBlacklist() {
        return nicknameBlacklist;
    }
    
    public ObservableSet<String> getRandomNames() {
        return randomNames;
    }
    
    public ObservableSet<String> getRandomSkins() {
        return randomSkins;
    }
    
    public String getVersion() {
        return version;
    }

    public abstract File getFolder();

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public GameLogManager getGameLogManager() {
        return gameLogManager;
    }

    public void setGameLogManager(GameLogManager gameLogManager) {
        this.gameLogManager = gameLogManager;
    }

    public static void logMessage(Level level, String mainMessage, String... debug) {
        Logger logger = NexusAPI.getApi().getLogger();
        logger.log(level, "----------- Nexus Log -----------");
        logger.log(level, mainMessage);
        if (debug != null) {
            for (String s : debug) {
                logger.log(level, s);
            }
        }
        logger.log(level, "---------------------------------");
    }

    public SQLDatabase getPrimaryDatabase() {
        return this.primaryDatabase;
    }

    public ToggleRegistry getToggleRegistry() {
        return toggleRegistry;
    }

    public URLClassLoader getLoader() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            return (URLClassLoader) classLoader;
        }
        return null;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public ClockManager getClockManager() {
        return clockManager;
    }

    public void setClockManager(ClockManager clockManager) {
        this.clockManager = clockManager;
    }

    public ServerRegistry<NexusServer> getServerRegistry() {
        return serverRegistry;
    }
    
    public NickPerms getNickPerms() {
        return nickPerms;
    }
}
