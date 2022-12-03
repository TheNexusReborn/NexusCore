package com.thenexusreborn.nexuscore.clock;

import com.thenexusreborn.nexuscore.clock.snapshot.ClockSnapshot;

import java.util.*;

public abstract class Clock<T extends ClockSnapshot> {
    private static final List<Clock<? extends ClockSnapshot>> CLOCKS = new ArrayList<>();
    
    protected long time;
    protected boolean paused, cancelled;
    
    protected ClockCallback<T> callback;
    
    public Clock() {
        CLOCKS.add(this);
    }
    
    public abstract void count();
    public abstract T createSnapshot();
    
    public boolean callback() {
        if (callback == null) {
            return true;
        }
        
        return callback.callback(createSnapshot());
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
    
    public void setCallback(ClockCallback<T> callback) {
        this.callback = callback;
    }
    
    public ClockCallback<T> getCallback() {
        return callback;
    }
    
    public static List<Clock<? extends ClockSnapshot>> getClocks() {
        return new ArrayList<>(CLOCKS);
    }
}