package com.thenexusreborn.nexuscore.clock.callback;

import com.thenexusreborn.nexuscore.clock.snapshot.ClockSnapshot;

import java.util.UUID;

public class CallbackHolder<T extends ClockSnapshot> {
    protected final ClockCallback<T> callback;
    protected final UUID callbackId;
    protected final long interval;
    protected long lastRun;
    protected boolean status = true;
    
    public CallbackHolder(ClockCallback<T> callback, UUID callbackId, long interval) {
        this.callback = callback;
        this.callbackId = callbackId;
        this.interval = interval;
    }
    
    public ClockCallback<T> getCallback() {
        return callback;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public long getLastRun() {
        return lastRun;
    }
    
    public void setLastRun(long lastRun) {
        this.lastRun = lastRun;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public boolean getStatus() {
        return status;
    }
}
