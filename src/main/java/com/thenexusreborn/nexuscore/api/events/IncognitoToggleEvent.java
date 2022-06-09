package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.api.player.NexusPlayer;
import org.bukkit.event.HandlerList;

public class IncognitoToggleEvent extends NexusPlayerEvent {
    
    private final boolean oldValue;
    private final boolean newValue;
    
    public IncognitoToggleEvent(NexusPlayer player, boolean oldValue, boolean newValue) {
        super(player);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public boolean getOldValue() {
        return oldValue;
    }
    
    public boolean getNewValue() {
        return newValue;
    }
    
    public static HandlerList getHandlerList()   {
        return handlers;
    }
}
