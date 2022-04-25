package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.nexuscore.player.NexusPlayer;
import org.bukkit.event.*;

public abstract class NexusPlayerEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private NexusPlayer nexusPlayer;
    
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
