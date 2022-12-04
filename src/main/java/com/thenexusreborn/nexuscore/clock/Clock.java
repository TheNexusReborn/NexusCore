package com.thenexusreborn.nexuscore.clock;

import com.starmediadev.starlib.TimeUnit;
import com.thenexusreborn.nexuscore.clock.snapshot.ClockSnapshot;

import java.util.*;

public abstract class Clock<T extends ClockSnapshot> {
    private static final List<Clock<? extends ClockSnapshot>> CLOCKS = new ArrayList<>();
    
    protected long time;
    protected boolean paused = true, cancelled;
    
    protected long callbackInterval;
    protected long lastCallback;
    
    protected ClockCallback<T> callback;
    
    public Clock() {
        CLOCKS.add(this);
    }
    
    public Clock(Options<T> options) {
        this();
        this.callbackInterval = options.callbackInterval;
        this.callback = options.callback;
        this.time = options.time;
    }
    
    public abstract void count();
    public abstract T createSnapshot();
    protected abstract boolean shouldCallback();
    
    public boolean callback() {
        if (callback == null) {
            return true;
        }
        
        if (callbackInterval > 0) {
            if (shouldCallback()) {
                lastCallback = this.time;
                return callback.callback(createSnapshot());
            }
        }
        
        return true;
    }
    
    public void setCallbackInterval(long callbackInterval) {
        this.callbackInterval = callbackInterval;
    }
    
    public void setCallbackInterval(long interval, TimeUnit unit) {
        setCallbackInterval(unit.toMilliseconds(interval));
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
    
    public void setCallback(ClockCallback<T> callback) {
        this.callback = callback;
    }
    
    public ClockCallback<T> getCallback() {
        return callback;
    }
    
    public static List<Clock<? extends ClockSnapshot>> getClocks() {
        return new ArrayList<>(CLOCKS);
    }
    
    public static class Options<T extends ClockSnapshot> {
        private long callbackInterval, time;
        private ClockCallback<T> callback;
    
        public Options<T> interval(long callbackInterval) {
            this.callbackInterval = callbackInterval;
            return this;
        }
        
        public Options<T> interval(long interval, TimeUnit unit) {
            return interval(unit.toMilliseconds(interval));
        }
    
        public Options<T> callback(ClockCallback<T> callback) {
            this.callback = callback;
            return this;
        }
        
        public Options<T> time(long time) {
            this.time = time;
            return this;
        }
    }
}