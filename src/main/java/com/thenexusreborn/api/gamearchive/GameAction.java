package com.thenexusreborn.api.gamearchive;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.sql.annotations.column.ColumnCodec;
import com.thenexusreborn.api.sql.annotations.column.ColumnType;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.api.sql.objects.codecs.ValueDataCodec;

import java.util.*;

@SuppressWarnings("ComparatorMethodParameterNotUsed")
@TableName("gameactions")
public class GameAction implements Comparable<GameAction> {
    public static final int CURRENT_VESRION = 3;
    
    private long id;
    private int version = CURRENT_VESRION;
    private long gameId;
    private long timestamp;
    private String type;
    @ColumnCodec(ValueDataCodec.class)
    @ColumnType("json")
    private Map<String, String> valueData = new LinkedHashMap<>();
    private String scope;
    
    private GameAction() {}
    
    public GameAction(long gameId, long timestamp, String type) {
        this.gameId = gameId;
        this.timestamp = timestamp;
        this.type = type;
        this.scope = "normal";
    }
    
    public GameAction(long timestamp, String type) {
        this.timestamp = timestamp;
        this.type = type;
        this.scope = "normal";
    }
    
    public GameAction(JsonObject json) {
        this.id = json.get("id").getAsLong();
        this.timestamp = json.get("timestamp").getAsLong();
        this.type = json.get("type").getAsString();
        this.version = json.get("version").getAsInt();
        
        if (this.version != CURRENT_VESRION) {
            this.version = CURRENT_VESRION; //TODO The changes between v2 and v3 are in SQL Structure and an importer is taking care of that
        }
        
        JsonObject dataObject = json.getAsJsonObject("data");
        for (Map.Entry<String, JsonElement> dataEntry : dataObject.entrySet()) {
            this.valueData.put(dataEntry.getKey(), dataEntry.getValue().getAsString());
        }
    }
    
    public JsonObject toJson() {
        JsonObject actionObject = new JsonObject();
        actionObject.addProperty("id", this.id);
        actionObject.addProperty("timestamp", getTimestamp());
        actionObject.addProperty("type", getType());
        actionObject.addProperty("version", getVersion());
        JsonObject dataObject = new JsonObject();
        getValueData().forEach(dataObject::addProperty);
        actionObject.add("data", dataObject);
        return actionObject;
    }

    public String getScope() {
        return scope;
    }

    public GameAction setScope(String scope) {
        this.scope = scope;
        return this;
    }
    
    public GameAction addValueData(String key, Object value) {
        if (value == null) {
            NexusAPI.getApi().getLogger().info("Null data for key " + key + " while adding value data to Game Action");
            return null;
        }
        
        valueData.put(key, value.toString());
        return this;
    }

    public Map<String, String> getValueData() {
        return valueData;
    }

    public int getVersion() {
        return version;
    }

    public long getGameId() {
        return gameId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getType() {
        return type;
    }
    
    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
    
    @Override
    public int compareTo(GameAction o) {
        if (this.timestamp > o.timestamp) {
            return 1;
        }
    
        return -1;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameAction that = (GameAction) o;
        return gameId == that.gameId && timestamp == that.timestamp && Objects.equals(type, that.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(gameId, timestamp, type);
    }
}
