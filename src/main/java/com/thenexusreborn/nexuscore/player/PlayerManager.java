package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.stats.*;
import com.thenexusreborn.nexuscore.tags.Tag;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.helper.MojangHelper;
import com.thenexusreborn.nexuscore.util.updater.*;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class PlayerManager implements Listener {
    
    public static final Set<UUID> NEXUS_TEAM = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"),
            UUID.fromString("fc6a3e38-c1c0-40a6-b7b9-152ffdadc053"), UUID.fromString("84c55f0c-2f09-4cf6-9924-57f536eb2228"))));
    
    private final NexusCore plugin;
    private Path directory;
    private Map<UUID, NexusPlayer> players = new HashMap<>();
    
    public PlayerManager(NexusCore plugin) {
        this.plugin = plugin;
        directory = FileSystems.getDefault().getPath(plugin.getDataFolder().toPath().toString(), "players");
    }
    
    public void setupMysql() throws SQLException {
        try (Connection connection = plugin.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players(version varchar(10), uuid varchar(36) NOT NULL, firstJoined varchar(100), lastLogin varchar(100), lastLogout varchar(100), playtime varchar(100), lastKnownName varchar(16), tag varchar(30), ranks varchar(10000));");
            statement.execute("CREATE TABLE IF NOT EXISTS stats(id int PRIMARY KEY NOT NULL AUTO_INCREMENT, uuid varchar(36), name varchar(100), value varchar(1000), created varchar(100), modified varchar(100));");
            statement.execute("CREATE TABLE IF NOT EXISTS statchanges(id int PRIMARY KEY NOT NULL AUTO_INCREMENT, uuid varchar(36), statName varchar(100), value varchar(100), operator varchar(50), timestamp varchar(100));");
            
            int version = 0;
            boolean convert = false;
            ResultSet versionSet = statement.executeQuery("select version from players;");
            while (versionSet.next()) {
                int v = Integer.parseInt(versionSet.getString("version"));
                if (v < NexusPlayer.version) {
                    version = v;
                    convert = true;
                    break;
                }
            }
            
            if (convert) {
                ResultSet resultSet = statement.executeQuery("select uuid from players;");
                while (resultSet.next()) {
                    String rawuuid = resultSet.getString("uuid");
                    UUID uuid = UUID.fromString(rawuuid);
                    loadFromMySQL(uuid);
                }
                
                if (version == 2) {
                    statement.execute("alter table players add column tag VARCHAR(30) after lastKnownName;");
                    statement.execute("alter table players add column lastLogout varchar(100) after lastLogin");
                }
    
                for (NexusPlayer player : this.players.values()) {
                    saveToMySQL(player);
                }
                
                this.players.clear();
            }
        }
    }
    
    public boolean hasData(UUID uuid) {
        try (Connection connection = plugin.getConnection()) {
            try (Statement queryStatement = connection.createStatement()) {
                ResultSet resultSet = queryStatement.executeQuery("SELECT * FROM players where uuid='" + uuid.toString() + "';");
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public NexusPlayer loadFromMySQL(UUID uuid) {
        try (Connection connection = plugin.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet playerResultSet = statement.executeQuery("SELECT * FROM players WHERE uuid='" + uuid.toString() + "';");
            if (playerResultSet.next()) {
                int version = Integer.parseInt(playerResultSet.getString("version"));
                long firstJoined = 0, lastLogin = 0, lastLogout = 0, playtime = 0;
                String lastKnownName = "", rawRanks;
                Map<Rank, Long> ranks = new TreeMap<>();
                Tag tag = null;
                if (version >= 2) {
                    firstJoined = Long.parseLong(playerResultSet.getString("firstJoined"));
                    lastLogin = Long.parseLong(playerResultSet.getString("lastLogin"));
                    playtime = Long.parseLong(playerResultSet.getString("playtime"));
                    lastKnownName = playerResultSet.getString("lastKnownName");
                    rawRanks = playerResultSet.getString("ranks");
                    if (rawRanks.contains(",")) {
                        String[] rankList = rawRanks.split(",");
                        for (String rl : rankList) {
                            String[] rankSplit = rl.split("=");
                            ranks.put(Rank.valueOf(rankSplit[0]), Long.parseLong(rankSplit[1]));
                        }
                    } else {
                        String[] rankSplit = rawRanks.split("=");
                        ranks.put(Rank.valueOf(rankSplit[0]), Long.parseLong(rankSplit[1]));
                    }
                }
                
                if (version >= 3) {
                    tag = plugin.getTagManager().getTag(playerResultSet.getString("tag"));
                    lastLogout = Long.parseLong(playerResultSet.getString("lastLogout"));
                }
                
                NexusPlayer nexusPlayer = new NexusPlayer(uuid, ranks, firstJoined, lastLogin, lastLogout, playtime, lastKnownName, tag);
                this.players.put(uuid, nexusPlayer);
    
                ResultSet statsResultSet = statement.executeQuery("select * from stats where '" + uuid + "';");
                while (statsResultSet.next()) {
                    int id = statsResultSet.getInt("id");
                    String name = statsResultSet.getString("name");
                    String rawValue = statsResultSet.getString("value");
                    long created = Long.parseLong(statsResultSet.getString("created"));
                    long modified = Long.parseLong(statsResultSet.getString("modified"));
                    
                    if (!StatRegistry.isValidStat(name)) {
                        continue;
                    }
                    
                    Stat<? extends Number> stat;
                    if (StatRegistry.isIntegerStat(name)) {
                        stat = StatRegistry.instantiateIntegerStat(id, name, uuid, Integer.parseInt(rawValue), created, modified);
                    } else if (StatRegistry.isDoubleStat(name)) {
                        stat = StatRegistry.instantiateDoubleStat(id, name, uuid, Double.parseDouble(rawValue), created, modified);
                    } else {
                        continue;
                    }
    
                    nexusPlayer.addStat((Stat<Number>) stat);
                }
    
                ResultSet statChangesResultSet = statement.executeQuery("select * from statchanges where '" + uuid + "'");
                while (statChangesResultSet.next()) {
                    int id = statChangesResultSet.getInt("id");
                    String name = statChangesResultSet.getString("statName");
                    String rawValue = statChangesResultSet.getString("value");
                    Operator operator = Operator.valueOf(statChangesResultSet.getString("operator"));
                    long timestamp = Long.parseLong(statChangesResultSet.getString("timestamp"));
                    Number value;
                    if (StatRegistry.isIntegerStat(name)) {
                        value = Integer.parseInt(rawValue);
                    } else if (StatRegistry.isDoubleStat(name)) {
                        value = Double.parseDouble(rawValue);
                    } else {
                        continue;
                    }
                    
                    StatChange<Number> statChange = new StatChange<>(id, uuid, name, value, operator, timestamp);
                    nexusPlayer.addStatChange(statChange);
                }
                return nexusPlayer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void saveToMySQL(NexusPlayer player) {
        try (Connection connection = plugin.getConnection()) {
            boolean exists = false;
            try (Statement queryStatement = connection.createStatement()) {
                ResultSet existingResultSet = queryStatement.executeQuery("SELECT * FROM players WHERE uuid='" + player.getUniqueId() + "';");
                StringBuilder sb = new StringBuilder();
                for (Entry<Rank, Long> entry : player.getRanks().entrySet()) {
                    sb.append(entry.getKey().name()).append("=").append(entry.getValue()).append(",");
                }
                
                String ranks = sb.substring(0, sb.toString().length() - 1);
                String sql;
                if (!existingResultSet.next()) {
                    sql = "INSERT INTO players(version, uuid, firstJoined, lastLogin, lastLogout, playtime, lastKnownName, ranks, tag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                } else {
                    sql = "UPDATE players SET version=?, uuid=?, firstJoined=?, lastLogin=?, lastLogout=?, playtime=?, lastKnownName=?, ranks=?, tag=? WHERE uuid='" + player.getUniqueId() + "';";
                }
                
                try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
                    insertStatement.setString(1, NexusPlayer.version + "");
                    insertStatement.setString(2, player.getUniqueId().toString());
                    insertStatement.setString(3, player.getFirstJoined() + "");
                    insertStatement.setString(4, player.getLastLogin() + "");
                    insertStatement.setString(5, player.getLastLogout() + "");
                    insertStatement.setString(6, player.getPlayTime() + "");
                    insertStatement.setString(7, player.getLastKnownName());
                    insertStatement.setString(8, ranks);
                    if (player.getTag() != null) {
                        insertStatement.setString(9, player.getTag().getName());
                    } else {
                        insertStatement.setString(9, "null");
                    }
                    insertStatement.execute();
                }
    
//                statement.execute("statchanges(id int, uuid varchar(36), statName varchar(100), value varchar(100), operator varchar(50), timestamp varchar(100));");
                for (Stat<?> stat : player.getStats().values()) {
                    String statSql;
                    if (stat.getId() > 0) {
                        try (PreparedStatement statement = connection.prepareStatement("update stats set value=?, modified=? where id='" + stat.getId() + "'")) {
                            statement.setString(1, stat.getValue().toString());
                            statement.setString(2, stat.getModified() + "");
                            statement.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement statement = connection.prepareStatement("insert into stats(uuid, name, value, created, modified) values(?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                            statement.setString(1, player.getUniqueId().toString());
                            statement.setString(2, stat.getName());
                            statement.setString(3, stat.getValue().toString());
                            statement.setString(4, stat.getCreated() + ""); 
                            statement.setString(5, stat.getModified() + "");
                            statement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveToMySQLAsync(NexusPlayer player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveToMySQL(player);
            }
        }.runTaskAsynchronously(plugin);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String originalJoinMessage = e.getJoinMessage();
        e.setJoinMessage(null);
        getNexusPlayerAsync(e.getPlayer().getUniqueId(), nexusPlayer -> {
            long start = System.nanoTime();
            nexusPlayer.setLastLogin(System.currentTimeMillis());
            if (nexusPlayer.getFirstJoined() == 0) {
                nexusPlayer.setFirstJoined(System.currentTimeMillis());
            }
            NexusScoreboard nexusScoreboard = new NexusScoreboard(nexusPlayer);
            nexusScoreboard.init();
            e.getPlayer().setScoreboard(nexusScoreboard.getScoreboard());
            nexusPlayer.setScoreboard(nexusScoreboard);
            players.put(e.getPlayer().getUniqueId(), nexusPlayer);
            nexusPlayer.sendMessage("&6&l>> &dWelcome to &5&lThe Nexus Reborn&5!");
            nexusPlayer.sendMessage("&6&l>> &dThis server is a project to bring back TheNexusMC, as least, some of it.");
            nexusPlayer.sendMessage("&6&l>> &dWe are currently in &aPre-Alpha &dso expect some bugs and instability, as well as a lack of features.");
            nexusPlayer.sendMessage("&6&l>> &dIf you would like to support us, please go to &eshop.thenexusreborn.com &dThat would mean a lot to us.");
            NexusPlayerLoadEvent nexusPlayerLoadEvent = new NexusPlayerLoadEvent(nexusPlayer, originalJoinMessage);
            Bukkit.getPluginManager().callEvent(nexusPlayerLoadEvent);
            String joinMessage = nexusPlayerLoadEvent.getJoinMessage();
            if (joinMessage != null) {
                Bukkit.broadcastMessage(MCUtils.color(joinMessage));
            }
    
            for (String statName : StatRegistry.getStats()) {
                if (!nexusPlayer.hasStat(statName)) {
                    nexusPlayer.setStat(statName, 0, Operator.ADD);
                }
            }
            long end = System.nanoTime();
        });
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        NexusPlayer nexusPlayer = this.players.get(e.getPlayer().getUniqueId());
        if (nexusPlayer != null) {
            nexusPlayer.setLastLogout(System.currentTimeMillis());
            saveToMySQLAsync(nexusPlayer);
            this.players.remove(e.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() == UpdateType.TICK) {
            for (NexusPlayer nexusPlayer : this.players.values()) {
                if (nexusPlayer.getPlayer() != null) {
                    nexusPlayer.incrementPlayTime();
                }
            }
        }
    }
    
    public void getNexusPlayerAsync(UUID uniqueId, Consumer<NexusPlayer> action) {
        new BukkitRunnable() {
            @Override
            public void run() {
                NexusPlayer nexusPlayer;
                if (players.containsKey(uniqueId)) {
                    nexusPlayer = players.get(uniqueId);
                } else if (hasData(uniqueId)) {
                    nexusPlayer = loadFromMySQL(uniqueId);
                } else {
                    nexusPlayer = new NexusPlayer(uniqueId);
                    try {
                        String name = MojangHelper.getNameFromUUID(uniqueId);
                        nexusPlayer.setLastKnownName(name);
                    }  catch (Exception e) {
                        plugin.getLogger().severe("Could not get " + uniqueId + "'s name from the Mojang API");
                    }
                    nexusPlayer.setRank(Rank.MEMBER, -1);
                }
                
                updateNexusTeamRank(nexusPlayer);
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        players.put(uniqueId, nexusPlayer);
                        action.accept(nexusPlayer);
                    }
                }.runTaskLater(plugin, 1L);
            }
        }.runTaskAsynchronously(plugin);
    }
    
    public void getNexusPlayerAsync(String name, Consumer<NexusPlayer> action) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (NexusPlayer np : players.values()) {
                    if (np.getName().equalsIgnoreCase(name)) {
                        action.accept(np);
                        return;
                    }
                }
    
                try (Connection connection = plugin.getConnection()) {
                    try (Statement queryStatement = connection.createStatement()) {
                        ResultSet resultSet = queryStatement.executeQuery("SELECT uuid FROM players WHERE lastKnownName='" + name + "'';");
                        if (resultSet.next()) {
                            getNexusPlayerAsync(UUID.fromString(resultSet.getString("uuid")), action);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    private void updateNexusTeamRank(NexusPlayer nexusPlayer) {
        if (NEXUS_TEAM.contains(nexusPlayer.getUniqueId()) && nexusPlayer.getRank() != Rank.NEXUS) {
            nexusPlayer.setRank(Rank.NEXUS, -1);
        }
        
        if (!NEXUS_TEAM.contains(nexusPlayer.getUniqueId()) && nexusPlayer.getRank() == Rank.NEXUS) {
            nexusPlayer.setRank(Rank.MEMBER, -1);
        }
    }
    
    public NexusPlayer getNexusPlayer(UUID uniqueId) {
        NexusPlayer nexusPlayer;
        if (this.players.containsKey(uniqueId)) {
            nexusPlayer = this.players.get(uniqueId);
        } else if (hasData(uniqueId)) {
            nexusPlayer = loadFromMySQL(uniqueId);
        } else {
            nexusPlayer = new NexusPlayer(uniqueId);
            try {
                String name = MojangHelper.getNameFromUUID(uniqueId);
                nexusPlayer.setLastKnownName(name);
            }  catch (Exception e) {
                plugin.getLogger().severe("Could not get " + uniqueId + "'s name from the Mojang API");
            }
            nexusPlayer.setRank(Rank.MEMBER, -1);
            this.players.put(nexusPlayer.getUniqueId(), nexusPlayer);
        }
        
        updateNexusTeamRank(nexusPlayer);
        return nexusPlayer;
    }
    
    public void saveData() {
        for (NexusPlayer nexusPlayer : this.players.values()) {
            saveToMySQL(nexusPlayer);
        }
    }
    
    public NexusPlayer getNexusPlayer(String name) {
        for (NexusPlayer nexusPlayer : this.players.values()) {
            if (nexusPlayer.getName().equalsIgnoreCase(name)) {
                return nexusPlayer;
            }
        }
        
        try (Connection connection = plugin.getConnection()) {
            try (PreparedStatement queryStatement = connection.prepareStatement("SELECT uuid FROM players WHERE lastKnownName='?';")) {
                queryStatement.setString(1, name);
                ResultSet resultSet = queryStatement.executeQuery();
                if (resultSet.next()) {
                    return getNexusPlayer(UUID.fromString(resultSet.getString("uuid")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void pushStatChangeAsync(StatChange<?> statChange) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection connection = plugin.getConnection(); PreparedStatement statement = connection.prepareStatement("insert into statchanges(uuid, statName, value, operator, timestamp) values (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, statChange.getUuid().toString());
                    statement.setString(2, statChange.getStatName());
                    statement.setString(3, statChange.getValue().toString());
                    statement.setString(4, statChange.getOperator().name());
                    statement.setString(5, statChange.getTimestamp() + "");
                    statement.executeUpdate();
    
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    generatedKeys.next();
                    int key = generatedKeys.getInt(1);
                    statChange.setId(key);
                } catch (SQLException e) {
                   e.printStackTrace(); 
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    public void pushStatAsync(Stat<?> stat) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection connection = plugin.getConnection()) {
                    if (stat.getId() > 0) {
                        try (Statement statement = connection.createStatement()) {
                            ResultSet resultSet = statement.executeQuery("select * from stats where id='" + stat.getId() + "';");
                            if (resultSet.next()) {
                                try (PreparedStatement preparedStatement = connection.prepareStatement("update stats set value=?, modified=? where id='" +stat.getId() + "'")) {
                                    preparedStatement.setString(1, stat.getValue().toString());
                                    preparedStatement.setString(2, stat.getModified() + "");
                                    preparedStatement.executeUpdate();
                                    return;
                                }
                            }
                        }
                    }
    
                    try (PreparedStatement statement = connection.prepareStatement("insert into stats(uuid, name, value, created, modified) values (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                        statement.setString(1, stat.getUuid().toString());
                        statement.setString(2, stat.getName());
                        statement.setString(3, stat.getValue().toString());
                        statement.setString(4, stat.getCreated() + "");
                        statement.setString(5, stat.getModified() + "");
                        statement.executeUpdate();
        
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        generatedKeys.next();
                        stat.setId(generatedKeys.getInt(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    public void removeStatChangeAsync(StatChange<?> statChange) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection connection = plugin.getConnection(); Statement statement = connection.createStatement()) {
                    statement.executeUpdate("delete from statchanges where id='" + statChange.getId() + "'");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
