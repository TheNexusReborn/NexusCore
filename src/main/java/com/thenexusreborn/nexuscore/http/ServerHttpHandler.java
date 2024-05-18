package com.thenexusreborn.nexuscore.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.nexuscore.NexusCore;

import java.io.IOException;
import java.io.OutputStream;

public class ServerHttpHandler implements HttpHandler {
    
    private NexusCore plugin;
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ServerHttpHandler(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "";
        String rawUrl = t.getRequestURI().toString();
        String[] urlParamSplit = rawUrl.split("\\?");
        if (urlParamSplit.length > 1) {
            String[] keyValueSplit = urlParamSplit[1].split("=");
            if (keyValueSplit[0].equalsIgnoreCase("name")) {
                NexusServer server = NexusAPI.getApi().getServerRegistry().get(keyValueSplit[1]);
                if (server == null) {
                    response = "Not a valid server name";
                } else {
                    response = GSON.toJson(new JsonParser().parse(server.getState()));
                }
            }
        }
        
        if (response.isEmpty()) {
            response = "Invalid arguments";
        }
        
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}