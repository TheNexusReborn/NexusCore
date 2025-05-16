package com.thenexusreborn.api.server;

import com.stardevllc.observable.property.IntegerProperty;
import com.stardevllc.observable.property.ObjectProperty;
import com.stardevllc.observable.property.StringProperty;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract sealed class NexusServer permits ProxyServer, InstanceServer, VirtualServer {
    protected final StringProperty name; //Server Name
    protected final ObjectProperty<ServerType> type; //Server Type, effectively final
    protected final StringProperty mode; //Things like hub, sg or other gamemodes, effectively final
    protected final StringProperty status; //Status like online, offline, error etc...
    protected final StringProperty state; //Format determined by plugin, different information about the server
    
    protected final IntegerProperty maxPlayers; //Maximum of players allowed.
    protected final Set<UUID> players; //Players currently in this server.

    public NexusServer(String name, ServerType type, String mode, int maxPlayers) {
        this.name = new StringProperty(this, "name", name);
        this.type = new ObjectProperty<>(ServerType.class, this, "type", type);
        this.mode = new StringProperty(this, "mode", mode);
        this.status = new StringProperty(this, "status", "");
        this.state = new StringProperty(this, "state", "");
        this.maxPlayers = new IntegerProperty(this, "maxPlayers", maxPlayers);
        this.players = new HashSet<>();
    }
    
    public abstract void join(NexusPlayer player);
    public abstract void quit(NexusPlayer player);
    
    public void join(UUID uuid) {
        if (uuid == null) {
            return;
        }
        
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
        if (nexusPlayer == null) {
            return;
        }
        
        join(nexusPlayer);
    }
    
    public void quit(UUID uuid) {
        if (uuid == null) {
            return;
        }
        
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
        if (nexusPlayer == null) {
            this.players.remove(uuid);
            return;
        }
        
        quit(nexusPlayer);
    }
    
    public abstract void onStart();
    public abstract void onStop();
    
    public boolean recalculateVisibility(UUID player, UUID otherPlayer) {
        throw new UnsupportedOperationException("");
    }

    public String getName() {
        return name.get();
    }

    public ServerType getType() {
        return type.get();
    }

    public String getMode() {
        return mode.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getState() {
        return state.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public int getMaxPlayers() {
        return maxPlayers.get();
    }
    
    public void addPlayer(UUID player) {
        this.players.add(player);
    }
    
    public void removePlayer(UUID player) {
        this.players.remove(player);
    }

    public StringProperty nameProperty() {
        return this.name;
    }
    
    public ObjectProperty<ServerType> typeProperty() {
        return this.type;
    }
    
    public StringProperty statusProperty() {
        return this.status;
    }
    
    public StringProperty stateProperty() {
        return state;
    }
    
    public IntegerProperty maxPlayersProperty() {
        return maxPlayers;
    }
    
    public Set<UUID> getPlayers() {
        return players;
    }
    
    public void teleportToSpawn(UUID uuid) {
        
    }
}