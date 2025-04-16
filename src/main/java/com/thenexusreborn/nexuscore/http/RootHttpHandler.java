package com.thenexusreborn.nexuscore.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thenexusreborn.nexuscore.NexusCore;

import java.io.IOException;
import java.io.OutputStream;

public class RootHttpHandler implements HttpHandler {
    
    private NexusCore plugin;
    
    public RootHttpHandler(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "<h1>Hello from Nexus API</h1>";
        
        t.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}