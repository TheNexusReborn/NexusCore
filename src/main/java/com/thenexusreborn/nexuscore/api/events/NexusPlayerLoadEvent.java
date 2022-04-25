package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.nexuscore.player.NexusPlayer;
import org.bukkit.event.HandlerList;

public class NexusPlayerLoadEvent extends NexusPlayerEvent {
    
    private static final HandlerList handlers = new HandlerList();
    
    private String joinMessage;
    
    public NexusPlayerLoadEvent(NexusPlayer nexusPlayer, String joinMessage) {
        super(nexusPlayer);
        this.joinMessage = joinMessage;
    }
    
    public String getJoinMessage() {
        return joinMessage;
    }
    
    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList()   {
        return handlers;
    }
}
