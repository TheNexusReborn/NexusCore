package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.api.player.*;
import org.bukkit.event.*;

public class ToggleChangeEvent extends NexusPlayerEvent implements Cancellable {
    
    private final Toggle toggle;
    private final boolean oldValue;
    private final boolean newValue;
    private boolean cancelled;
    private String cancelReason;
    
    public ToggleChangeEvent(NexusPlayer nexusPlayer, Toggle toggle, boolean oldValue, boolean newValue) {
        super(nexusPlayer);
        this.toggle = toggle;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public Toggle getToggle() {
        return toggle;
    }
    
    public boolean oldValue() {
        return oldValue;
    }
    
    public boolean newValue() {
        return newValue;
    }
    
    public static HandlerList getHandlerList()   {
        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
    
    public String getCancelReason() {
        return cancelReason;
    }
    
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
