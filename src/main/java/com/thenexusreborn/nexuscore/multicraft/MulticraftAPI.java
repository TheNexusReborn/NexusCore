package com.thenexusreborn.nexuscore.multicraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thenexusreborn.nexuscore.multicraft.data.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MulticraftAPI {
    private String url;
    private String user;
    private String key;
    
    private static MulticraftAPI INSTANCE;
    
    static {
        File directory = new File(System.getProperty("user.dir"));
        File mcFile = new File(directory + File.separator + "multicraft.txt");
        if (!mcFile.exists()) {
            System.err.println("Could not find the multicraft.txt file!");
        } else {
            try (BufferedReader input = new BufferedReader(new FileReader(mcFile))) {
                String line = input.readLine();
                if (line != null && !line.isEmpty()) {
                    INSTANCE = new MulticraftAPI("https://firecraftmc.net/multicraft/api.php", "api", line);
                }
            } catch (Exception e) {
                System.out.println("Error loading MulcraftAPI: " + e.getMessage());
            }
        }
    }
    
    public static MulticraftAPI getInstance() {
        return INSTANCE;
    }
    
    private MulticraftAPI(String url, String user, String key) {
        this.url = url;
        this.user = user;
        this.key = key;
    }
    
    public void sendConsoleCommand(int server, String command) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("server_id", server + "");
        parameters.put("command", command);
        call("sendConsoleCommand", parameters);
    }
    
    public ServerList findServers(String field, String value) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("field", field);
        parameters.put("value", value);
        JsonObject result = call("listServers", parameters);
        return getServerListFromResult(result);
    }
    
    private ServerList getServerListFromResult(JsonObject result) {
        JsonObject serverResults = result.getAsJsonObject("Servers");
        ServerList servers = new ServerList();
        for (Entry<String, JsonElement> entry : serverResults.entrySet()) {
            servers.addServer(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
        }
        return servers;
    }
    
    public ServerStatus getServerStatus(int id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id + "");
        parameters.put("player_list", "true");
        JsonObject result = call("getServerStatus", parameters);
        Set<PlayerStatus> players = new HashSet<>();
        if (result == null) {
            ServerStatus serverStatus = new ServerStatus();
            serverStatus.serverId = id;
            serverStatus.status = "error";
            serverStatus.maxPlayers = 0;
            serverStatus.onlinePlayers = 0;
            serverStatus.players = players;
            return serverStatus;
        }
        JsonArray playersArray = result.getAsJsonArray("players");
        for (JsonElement jsonElement : playersArray) {
            JsonObject playerInfo = jsonElement.getAsJsonObject();
            PlayerStatus playerStatus = new PlayerStatus();
            playerStatus.ip = playerInfo.get("ip").getAsString();
            playerStatus.id = playerInfo.get("id").getAsInt();
            playerStatus.name = playerInfo.get("name").getAsString();
            players.add(playerStatus);
        }
        ServerStatus serverStatus = new ServerStatus();
        serverStatus.serverId = id;
        serverStatus.status = result.get("status").getAsString();
        serverStatus.onlinePlayers = result.get("onlinePlayers").getAsInt();
        serverStatus.maxPlayers = result.get("maxPlayers").getAsInt();
        serverStatus.players = players;
        return serverStatus;
    }
    
    public ServerList getServers() {
        JsonObject result = call("listServers", new HashMap<>());
        return getServerListFromResult(result);
    }
    
    public ServerInfo getServer(int id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id + "");
        JsonObject result = call("getServer", parameters);
        JsonObject si = result.getAsJsonObject("Server");
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.memory = si.get("memory").getAsInt();
        serverInfo.startMemory = si.get("start_memory").getAsInt();
        serverInfo.port = si.get("port").getAsInt();
        serverInfo.autoStart = si.get("autostart").getAsInt();
        serverInfo.defaultLevel = si.get("default_level").getAsInt();
        serverInfo.daemonId = si.get("daemon_id").getAsInt();
        serverInfo.announceSave = si.get("announce_save").getAsInt();
        serverInfo.kickDelay = si.get("kick_delay").getAsInt();
        serverInfo.suspended = si.get("suspended").getAsInt();
        serverInfo.autosave = si.get("autosave").getAsInt();
        serverInfo.players = si.get("players").getAsInt();
        serverInfo.id = si.get("id").getAsInt();
        serverInfo.diskQuota = si.get("disk_quota").getAsInt();
        serverInfo.ip = si.get("ip").getAsString();
        serverInfo.world = si.get("world").getAsString();
        serverInfo.jarfile = si.get("jarfile").getAsString();
        serverInfo.jardir = si.get("jardir").getAsString();
        serverInfo.template = si.get("template").getAsString();
        serverInfo.setup = si.get("setup").getAsString();
        serverInfo.prevJarfile = si.get("prev_jarfile").getAsString();
        serverInfo.params = si.get("params").getAsString();
        serverInfo.crashCheck = si.get("crash_check").getAsString();
        serverInfo.domain = si.get("domain").getAsString();
        serverInfo.name = si.get("name").getAsString();
        serverInfo.dir = si.get("dir").getAsString();
        return serverInfo;
    }
    
    public void startServer(int id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id + "");
        call("startServer", parameters);
    }
    
    public void stopServer(int id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id + "");
        call("stopServer", parameters);
    }
    
    public void restartServer(int id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id + "");
        call("restartServer", parameters);
    }
    
    public void killServer(int id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id + "");
        call("killServer", parameters);
    }
    
    @SuppressWarnings("deprecation")
    public JsonObject call(String method, Map<String, String> parameters) {
        parameters = new HashMap<>(parameters);
        try {
            URL url = new URL(this.url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            
            // Add neccessary parameters
            parameters.put("_MulticraftAPIMethod", method);
            parameters.put("_MulticraftAPIUser", user);
            
            StringBuilder apiKeySalt = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            for (Entry<String, String> param : parameters.entrySet()) {
                final String parameterKey = param.getKey();
                final String parameterValue = param.getValue();
                
                // The api key is hashed with all params put after each other (with their values)
                apiKeySalt.append(parameterKey).append(parameterValue);
                query.append("&").append(URLEncoder.encode(parameterKey)).append("=").append(URLEncoder.encode(parameterValue));
            }
            
            // Append apikey (build by a hash of the apikey and the concatinated params as salt)
            query.append("&_MulticraftAPIKey=").append(MulticraftAPI.getMulticraftEncodedAPIKey(apiKeySalt.toString(), this.key));
            
            // Write all query params
            DataOutputStream output = new DataOutputStream(conn.getOutputStream());
            output.writeBytes(query.toString());
            output.close();
            
            JsonParser parser = new JsonParser();
            JsonObject result = parser.parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            
            if (!(result.get("success").getAsBoolean())) {
                JsonArray errors = result.get("errors").getAsJsonArray();
                StringBuilder exc = new StringBuilder();
                for (JsonElement jsonElement : errors) {
                    exc.append(jsonElement.getAsString());
                    exc.append(", ");
                }
                throw new Exception(exc.toString());
            } else if (result.get("data").isJsonObject()) {
                return result.get("data").getAsJsonObject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getMulticraftEncodedAPIKey(String parameterQuery, String apiKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hasher = Mac.getInstance("HmacSHA256");
        hasher.init(new SecretKeySpec(apiKey.getBytes(), "HmacSHA256"));
        
        byte[] hash = hasher.doFinal(parameterQuery.getBytes());
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }
}