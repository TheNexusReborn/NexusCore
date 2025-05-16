package com.thenexusreborn.api.server;

import com.stardevllc.observable.property.ObjectProperty;

import java.util.UUID;

public abstract non-sealed class VirtualServer extends NexusServer {
    
    protected final ObjectProperty<InstanceServer> parentServer;
    
    public VirtualServer(InstanceServer parent, String name, String mode, int maxPlayers) {
        super(name, ServerType.VIRTUAL, mode, maxPlayers);
        this.parentServer = new ObjectProperty<>(InstanceServer.class, this, "parentServer", parent);
        
        this.parentServer.addListener(e -> {
            if (e.oldValue() != null) {
                for (UUID player : players) {
                    e.oldValue().removePlayer(player);
                }
            }

            if (e.newValue() != null) {
                for (UUID player : players) {
                    e.newValue().addPlayer(player);
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