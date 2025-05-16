package com.thenexusreborn.api.sql.objects.codecs;

import com.google.gson.*;
import com.thenexusreborn.api.gamearchive.PlayerInfo;
import com.thenexusreborn.api.sql.objects.SqlCodec;

import java.util.HashSet;
import java.util.Set;

public class GamePlayerInfoCodec implements SqlCodec<Set<PlayerInfo>> {
    @Override
    public String encode(Object object) {
        Set<PlayerInfo> players = (Set<PlayerInfo>) object;
        
        JsonArray json = new JsonArray();
        for (PlayerInfo player : players) {
            json.add(player.toJson());
        }
        
        return json.toString();
    }
    
    @Override
    public Set<PlayerInfo> decode(String encoded) {
        JsonArray json = new JsonParser().parse(encoded).getAsJsonArray();
        
        Set<PlayerInfo> players = new HashSet<>();
        for (JsonElement jsonElement : json) {
            PlayerInfo playerInfo = new PlayerInfo(jsonElement.getAsJsonObject());
            players.add(playerInfo);
        }
        
        return players;
    }
}