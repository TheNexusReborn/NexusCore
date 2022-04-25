package com.thenexusreborn.nexuscore.multicraft.data;

import java.util.HashMap;
import java.util.Map;

public class ServerList implements MulticraftObject {
    private final Map<Integer, String> servers = new HashMap<>();
    
    public void addServer(int id, String name) {
        this.servers.put(id, name);
    }
    
    public Map<Integer, String> getServers() {
        return servers;
    }
}
