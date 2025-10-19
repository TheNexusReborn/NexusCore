package com.thenexusreborn.api.server;

import com.stardevllc.starlib.observable.property.readwrite.ReadWriteObjectProperty;

import java.util.UUID;

public abstract non-sealed class VirtualServer extends NexusServer {
    
    protected final ReadWriteObjectProperty<InstanceServer> parentServer;
    
    public VirtualServer(InstanceServer parent, String name, String mode, int maxPlayers) {
        super(name, ServerType.VIRTUAL, mode, maxPlayers);
        this.parentServer = new ReadWriteObjectProperty<>(this, "parentServer", InstanceServer.class);
        
        this.parentServer.addListener((source, oldValue, newValue) -> {
            if (oldValue != null) {
                for (UUID player : players) {
                    oldValue.removePlayer(player);
                }
            }

            if (newValue != null) {
                for (UUID player : players) {
                    newValue.addPlayer(player);
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