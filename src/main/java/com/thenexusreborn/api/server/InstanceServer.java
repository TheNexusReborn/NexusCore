package com.thenexusreborn.api.server;

import com.stardevllc.starlib.values.property.ObjectProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract non-sealed class InstanceServer extends NexusServer {

    protected final ObjectProperty<VirtualServer> primaryVirtualServer;
    private final ServerRegistry<VirtualServer> childServers = new ServerRegistry<>(VirtualServer.class);
    
    public InstanceServer(String name, String mode, int maxPlayers) {
        super(name, ServerType.INSTANCE, mode, maxPlayers);
        this.primaryVirtualServer = new ObjectProperty<>(this, "primaryVirtualServer", VirtualServer.class);
    }

    public ServerRegistry<VirtualServer> getChildServers() {
        return childServers;
    }

    public VirtualServer getPrimaryVirtualServer() {
        return primaryVirtualServer.get();
    }

    public void setPrimaryVirtualServer(VirtualServer primaryVirtualServer) {
        this.primaryVirtualServer.set(primaryVirtualServer);
    }

    @Override
    public Set<UUID> getPlayers() {
        Set<UUID> players = new HashSet<>(this.players);
        this.childServers.values().forEach(server -> players.addAll(server.getPlayers()));
        return players;
    }
}
