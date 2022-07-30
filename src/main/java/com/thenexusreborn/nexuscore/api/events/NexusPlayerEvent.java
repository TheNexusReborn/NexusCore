package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.api.player.NexusPlayer;
import org.bukkit.event.*;

public abstract class NexusPlayerEvent extends Event {
    
    protected static final HandlerList handlers = new HandlerList();
    
    protected final NexusPlayer nexusPlayer;
    
    public NexusPlayerEvent(NexusPlayer nexusPlayer) {
        this.nexusPlayer = nexusPlayer;
    }
    
    public NexusPlayer getNexusPlayer() {
        return nexusPlayer;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList()   {
        return handlers;
    }
}
