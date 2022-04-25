package com.thenexusreborn.nexuscore.util.helper;

import com.google.gson.*;

import java.io.*;
import java.net.URL;
import java.util.UUID;

public final class MojangHelper {
    
    private static StringBuilder getJsonBuffer(String urlString) throws IOException {
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder buffer = new StringBuilder();
        int read;
        char[] chars = new char[256];
        while ((read = reader.read(chars)) != -1) {
            buffer.append(chars, 0, read);
        }
        
        reader.close();
        return buffer;
    }
    
    
    public static JsonObject getJsonObject(String urlString) {
        try {
            StringBuilder buffer = getJsonBuffer(urlString);
            return (JsonObject) new JsonParser().parse(buffer.toString());
        } catch (Exception e) {
        }
        
        return null;
    }
    
    private static UUID formatUUID(String uuidString) {
        String finalUUIDString = uuidString.substring(0, 8) + "-";
        finalUUIDString += uuidString.substring(8, 12) + "-";
        finalUUIDString += uuidString.substring(12, 16) + "-";
        finalUUIDString += uuidString.substring(16, 20) + "-";
        finalUUIDString += uuidString.substring(20, 32);
        return UUID.fromString(finalUUIDString);
    }
    
    public static UUID getUUIDFromName(String username) {
        String s = "https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + (System.currentTimeMillis() / 1000);
        
        JsonObject json = getJsonObject(s);
        if (json == null) {
            return null;
        }
        JsonElement idObject = json.get("id");
        if (idObject == null) {
            return null;
        }
        
        return formatUUID(idObject.getAsString());
    }
    
    public static String getNameFromUUID(UUID uuid) {
        String profileURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "");
        JsonObject json = getJsonObject(profileURL);
        if (json == null) { return null; }
        JsonElement element = json.get("name");
        if (element == null) { return null; }
        return element.getAsString();
    }
}
