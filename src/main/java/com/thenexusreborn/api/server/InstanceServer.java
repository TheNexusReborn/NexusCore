package com.thenexusreborn.api.server;

import com.stardevllc.starlib.observable.property.readwrite.ReadWriteObjectProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract non-sealed class InstanceServer extends NexusServer {

    protected final ReadWriteObjectProperty<VirtualServer> primaryVirtualServer;
    private final ServerRegistry<VirtualServer> childServers = new ServerRegistry<>();
    
    public InstanceServer(String name, String mode, int maxPlayers) {
        super(name, ServerType.INSTANCE, mode, maxPlayers);
        this.primaryVirtualServer = new ReadWriteObjectProperty<>(this, "primaryVirtualServer", VirtualServer.class);
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
        this.childServers.getObjects().values().forEach(server -> players.addAll(server.getPlayers()));
        return players;
    }
}
