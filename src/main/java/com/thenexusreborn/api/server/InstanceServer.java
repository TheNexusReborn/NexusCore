package com.thenexusreborn.api.server;

import com.stardevllc.starlib.objects.key.Keys;
import com.stardevllc.starlib.values.property.ObjectProperty;
import com.thenexusreborn.api.NexusReborn;

import java.util.*;

public abstract non-sealed class InstanceServer extends NexusServer {

    protected final ObjectProperty<VirtualServer> primaryVirtualServer;
    private final ServerRegistry<VirtualServer> childServers;
    
    public InstanceServer(String name, String mode, int maxPlayers) {
        super(name, ServerType.INSTANCE, mode, maxPlayers);
        this.childServers = new ServerRegistry<>(VirtualServer.class, name + "_childservers", name, NexusReborn.getServerRegistry());
        this.primaryVirtualServer = new ObjectProperty<>(this, "primaryVirtualServer", VirtualServer.class);
        this.primaryVirtualServer.addChangeListener((v, o, n) -> {
            if (o != null) {
                childServers.remove(Keys.of(name + ":" + o.getName()));
            } 
            
            if (n != null) {
                childServers.register(Keys.of(name + ":" + n.getName()), n);
            }
        });
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
