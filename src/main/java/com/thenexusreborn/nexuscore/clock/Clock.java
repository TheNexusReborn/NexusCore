package com.thenexusreborn.nexuscore.clock;

import com.thenexusreborn.nexuscore.clock.callback.*;
import com.thenexusreborn.nexuscore.clock.snapshot.ClockSnapshot;

import java.util.*;

public abstract class Clock<T extends ClockSnapshot> {
    private static final List<Clock<? extends ClockSnapshot>> CLOCKS = new ArrayList<>();
    
    protected long time;
    protected boolean paused = true, cancelled;
    
    protected Map<UUID, CallbackHolder<T>> callbacks = new HashMap<>();
    
    public Clock() {
        CLOCKS.add(this);
    }
    
    public Clock(long time) {
        this();
        this.time = time;
    }
    
    public Clock(long time, ClockCallback<T> callback, long interval) {
        this(time);
        addCallback(callback, interval);
    }
    
    public abstract void count();
    public abstract T createSnapshot();
    protected abstract boolean shouldCallback(CallbackHolder<T> holder);
    
    public boolean callback() {
        if (this.callbacks.isEmpty()) {
            return true;
        }
    
        boolean result = false;
    
        T snapshot = createSnapshot();
        for (CallbackHolder<T> holder : this.callbacks.values()) {
            ClockCallback<T> callback = holder.getCallback();
            if (callback == null) {
                continue;
            }
            
            if (holder.getInterval() > 0) {
                if (shouldCallback(holder)) {
                    holder.setLastRun(time);
                    boolean callbackResult = callback.callback(snapshot);
                    if (!result) {
                        result = callbackResult;
                    }
                } else {
                    result = true;
                }
            }
        }
    
        return result;
    }
    
    public Clock<T> start() {
        unpause();
        return this;
    }
    
    public void pause() {
        this.paused = true;
    }
    
    public void unpause() {
        this.paused = false;
    }
    
    public void cancel() {
        CLOCKS.remove(this);
        this.cancelled = true;
    }
    
    public void uncancel() {
        CLOCKS.add(this);
        this.cancelled = false;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public long getTime() {
        return time;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void addTime(long time) {
        this.time += time;
    }
    
    public void removeTime(long time) {
        this.time -= time;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
    
    public UUID addCallback(ClockCallback<T> callback, long interval) {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (this.callbacks.containsKey(uuid));
        
        this.callbacks.put(uuid, new CallbackHolder<>(callback, uuid, interval));
        return uuid;
    }
    
    public ClockCallback<T> getCallback(UUID uuid) {
        CallbackHolder<T> holder = this.callbacks.get(uuid);
        if (holder != null) {
            return holder.getCallback();
        } 
        
        return null;
    }
    
    public static List<Clock<? extends ClockSnapshot>> getClocks() {
        return new ArrayList<>(CLOCKS);
    }
}