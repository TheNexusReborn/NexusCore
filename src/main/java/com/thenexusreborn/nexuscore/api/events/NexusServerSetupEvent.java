package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.server.VirtualServer;
import com.thenexusreborn.api.util.NetworkType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public class NexusServerSetupEvent extends Event {

    protected static final HandlerList handlers = new HandlerList();

    private NetworkType networkType;
    
    private InstanceServer server;
    
    private Map<String, VirtualServer> virtualServers = new HashMap<>();

    public NexusServerSetupEvent(NetworkType networkType) {
        this.networkType = networkType;
    }

    public void setServer(InstanceServer server) {
        this.server = server;
    }

    public InstanceServer getServer() {
        return server;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public Map<String, VirtualServer> getVirtualServers() {
        return virtualServers;
    }
    
    public void addVirtualServer(VirtualServer server) {
        this.virtualServers.put(server.getName().toLowerCase().replace(" ", "_"), server);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList()   {
        return handlers;
    }
}
