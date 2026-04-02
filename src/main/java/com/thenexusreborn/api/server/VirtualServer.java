package com.thenexusreborn.api.server;

import com.stardevllc.starlib.values.property.ObjectProperty;

import java.util.UUID;

public abstract non-sealed class VirtualServer extends NexusServer {
    
    protected final ObjectProperty<InstanceServer> parentServer;
    
    public VirtualServer(InstanceServer parent, String name, String mode, int maxPlayers) {
        super(name, ServerType.VIRTUAL, mode, maxPlayers);
        this.parentServer = new ObjectProperty<>(this, "parentServer", InstanceServer.class);
        
        this.parentServer.addChangeListener((v, o, n) -> {
            if (o != null) {
                for (UUID player : players) {
                    o.removePlayer(player);
                }
            }

            if (n != null) {
                for (UUID player : players) {
                    n.addPlayer(player);
                }
            }
        });
    }

    public VirtualServer(String name, String mode, int maxPlayers) {
        this(null, name, mode, maxPlayers);
    }

    public InstanceServer getParentServer() {
        return parentServer.get();
    }

    public void setParentServer(InstanceServer parentServer) {
        this.parentServer.set(parentServer);
    }
}