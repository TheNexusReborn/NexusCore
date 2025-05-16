package com.thenexusreborn.api.gamearchive;

import com.google.gson.*;
import com.stardevllc.helper.StringHelper;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.PlayerManager;
import com.thenexusreborn.api.sql.annotations.column.*;
import com.thenexusreborn.api.sql.annotations.table.TableHandler;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.api.sql.objects.codecs.GamePlayerInfoCodec;
import com.thenexusreborn.api.sql.objects.objecthandler.GamesObjectHandler;

import java.util.*;
import java.util.Map.Entry;

@TableName("games")
@TableHandler(GamesObjectHandler.class)
public class GameInfo implements Comparable<GameInfo> {
    private static final int CURRENT_VERSION = 2;
    
    public static final Set<String> playersWithNoUUID = new HashSet<>();
    
    private long id;
    private int version = CURRENT_VERSION;
    private long gameStart, gameEnd;
    private String serverName;
    @ColumnType("json")
    @ColumnCodec(GamePlayerInfoCodec.class)
    private Set<PlayerInfo> players = new HashSet<>();
    private String winner, mapName, settings, firstBlood;
    private int playerCount;
    private long length;
    @ColumnIgnored
    private final Set<GameAction> actions = new TreeSet<>();
    
    public GameInfo() {
    }
    
    public GameInfo(JsonObject json) {
        this.id = json.get("id").getAsLong();
        this.gameStart = json.get("start").getAsLong();
        this.gameEnd = json.get("end").getAsLong();
        this.serverName = json.get("server").getAsString();
        this.mapName = json.get("map").getAsString();
        this.playerCount = json.get("playercount").getAsInt();
        this.length = json.get("length").getAsLong();
        
        if (json.has("version")) {
            this.version = json.get("version").getAsInt();
            if (this.version != CURRENT_VERSION) {
                this.version = CURRENT_VERSION; //TODO This is just adding the version to the GameInfo, future things will have a convert method
            }
        }
        
        if (json.has("version")) {
            JsonArray playersArray = json.get("players").getAsJsonArray();
            for (JsonElement player : playersArray) {
                PlayerInfo playerInfo = new PlayerInfo(player.getAsJsonObject());
                this.players.add(playerInfo);
                if (playerInfo.getUniqueId() == null) {
                    playersWithNoUUID.add(playerInfo.getName());
                }
            }
        } else {
            JsonObject playersObject = json.get("players").getAsJsonObject();
            for (Entry<String, JsonElement> entry : playersObject.entrySet()) {
                String name = entry.getKey();
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(entry.getValue().getAsString());
                } catch (Throwable e) {}
                
                PlayerInfo playerInfo = new PlayerInfo(name, uuid);
                this.players.add(playerInfo);
                if (uuid == null) {
                    playersWithNoUUID.add(playerInfo.getName());
                }
            }
        }
        
        this.winner = json.get("winner").getAsString();
        this.firstBlood = json.get("firstblood").getAsString();
        
        JsonObject actionsObject = json.getAsJsonObject("actions");
        for (Map.Entry<String, JsonElement> actionEntry : actionsObject.entrySet()) {
            GameAction gameAction = new GameAction(actionEntry.getValue().getAsJsonObject());
            this.actions.add(gameAction);
        }
    }
    
    public JsonObject toJson() {
        JsonObject gameJson = new JsonObject();

        gameJson.addProperty("id", getId());
        gameJson.addProperty("version", this.version);
        gameJson.addProperty("start", getGameStart());
        gameJson.addProperty("end", getGameEnd());
        gameJson.addProperty("server", getServerName());
        gameJson.addProperty("map", getMapName());
        gameJson.addProperty("playercount", getPlayerCount());
        gameJson.addProperty("length", getLength());

        JsonArray playersArray = new JsonArray();

        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
        
        for (PlayerInfo player : this.players) {
            playersArray.add(player.toJson());
        }

        gameJson.add("players", playersArray);

        if (!StringHelper.isEmpty(getWinner())) {
            UUID uuid = playerManager.getUUIDFromName(getWinner());
            if (uuid != null) {
                gameJson.addProperty("winner", uuid.toString());
            } else {
                gameJson.addProperty("winner", getWinner());
            }
        }

        if (!StringHelper.isEmpty(getFirstBlood())) {
            UUID uuid = playerManager.getUUIDFromName(getFirstBlood());
            if (uuid != null) {
                gameJson.addProperty("firstblood", uuid.toString());
            } else {
                gameJson.addProperty("firstblood", getFirstBlood());
            }
        }

        JsonObject actionsJson = new JsonObject();
        for (GameAction action : getActions()) {
            actionsJson.add(String.valueOf(action.getTimestamp()), action.toJson());
        }
        gameJson.add("actions", actionsJson);
        return gameJson;
    }
    
    public GameInfo(long id, long gameStart, long gameEnd, String serverName, Set<PlayerInfo> players, String winner, String mapName, String settings, String firstBlood, int playerCount, long length) {
        this.id = id;
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.serverName = serverName;
        this.players = players;
        this.winner = winner;
        this.mapName = mapName;
        this.settings = settings;
        this.firstBlood = firstBlood;
        this.playerCount = playerCount;
        this.length = length;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    public String getWinner() {
        return winner;
    }
    
    public void setWinner(String winner) {
        this.winner = winner;
    }
    
    public long getId() {
        return id;
    }
    
    public long getGameStart() {
        return gameStart;
    }
    
    public long getGameEnd() {
        return gameEnd;
    }
    
    public Set<PlayerInfo> getPlayers() {
        return players;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public String getSettings() {
        return settings;
    }
    
    public String getFirstBlood() {
        return firstBlood;
    }
    
    public int getPlayerCount() {
        return playerCount;
    }
    
    public long getLength() {
        return length;
    }
    
    public Set<GameAction> getActions() {
        return actions;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setGameStart(long gameStart) {
        this.gameStart = gameStart;
    }
    
    public void setGameEnd(long gameEnd) {
        this.gameEnd = gameEnd;
    }
    
    public void setPlayers(PlayerInfo... players) {
        if (players != null) {
            this.players.clear();
            this.players.addAll(Arrays.asList(players));
        }
    }
    
    public void setPlayers(Set<PlayerInfo> players) {
        this.players.clear();
        this.players.addAll(players);
    }
    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
    
    public void setSettings(String settings) {
        this.settings = settings;
    }
    
    public void setFirstBlood(String firstBlood) {
        this.firstBlood = firstBlood;
    }
    
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
    
    public void setLength(long length) {
        this.length = length;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameInfo gameInfo = (GameInfo) o;
        return gameStart == gameInfo.gameStart && gameEnd == gameInfo.gameEnd && playerCount == gameInfo.playerCount && length == gameInfo.length && Objects.equals(serverName, gameInfo.serverName) && Objects.equals(winner, gameInfo.winner) && Objects.equals(mapName, gameInfo.mapName) && Objects.equals(settings, gameInfo.settings) && Objects.equals(firstBlood, gameInfo.firstBlood);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(gameStart, gameEnd, serverName, winner, mapName, settings, firstBlood, playerCount, length);
        result = 31 * result;
        return result;
    }
    
    @Override
    public int compareTo(GameInfo o) {
        return Long.compare(this.id, o.id);
    }
}
