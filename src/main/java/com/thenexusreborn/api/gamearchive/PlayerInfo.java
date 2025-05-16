package com.thenexusreborn.api.gamearchive;

import com.google.gson.JsonObject;

import java.util.UUID;

public class PlayerInfo {
    private String name;
    private UUID uniqueId;
    private boolean nicked;
    
    public PlayerInfo(String name, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.nicked = false;
    }
    
    public PlayerInfo(String name, UUID uniqueId, boolean nicked) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.nicked = nicked;
    }
    
    public PlayerInfo(JsonObject jsonObject) {
        this.name = jsonObject.get("name").getAsString();
        
        if (jsonObject.has("uniqueid")) {
            this.uniqueId = UUID.fromString(jsonObject.get("uniqueid").getAsString());
        }
        
        if (jsonObject.has("nicked")) {
            this.nicked = jsonObject.get("nicked").getAsBoolean();
        }
    }
    
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", this.name);
        if (this.uniqueId != null) {
            jsonObject.addProperty("uniqueid", this.uniqueId.toString());
        }
        
        if (this.nicked) {
            jsonObject.addProperty("nicked", String.valueOf(this.nicked));
        }
        
        return jsonObject;
    }
    
    public String getName() {
        return name;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public boolean isNicked() {
        return nicked;
    }
}
