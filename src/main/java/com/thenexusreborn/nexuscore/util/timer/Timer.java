package com.thenexusreborn.nexuscore.util.timer;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.ReturnableCallback;

public class Timer {
    private final ReturnableCallback<TimerSnapshot, Boolean> callback;
    private boolean cancelled;
    private long length = -1;
    private long paused = -1;
    private boolean running;
    private long time;
    
    public Timer(ReturnableCallback<TimerSnapshot, Boolean> callback) {
        this.callback = callback;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void count() {
        if (!running) {
            return;
        }
        time -= 50;
    }
    
    public ReturnableCallback<TimerSnapshot, Boolean> getCallback() {
        return callback;
    }
    
    public void reset() {
        setLength(length);
    }
    
    public void setRawTime(long l) {
        this.time = l;
    }
    
    public void setRawLength(long l) {
        this.length = l;
    }
    
    public boolean isPaused() {
        return this.paused > -1;
    }
    
    public void setPaused(boolean paused) {
        if (!paused) {
            setLength(getTimeLeft());
            this.paused = -1;
        } else {
            this.paused = System.currentTimeMillis();
        }
        running = !paused;
    }
    
    public long getTimeLeft() {
        return time;
    }
    
    public void addLength(long l) {
        this.length += l;
        this.time += l;
    }
    
    public void addTime(long l) {
        setTime(this.time + l);
    }
    
    public void setTime(long l) {
        if (l > this.length) {
            throw new IllegalArgumentException("The new time cannot be greather than the length");
        }
        
        this.time = l;
    }
    
    public Timer run(long length) {
        setLength(length);
        run();
        return this;
    }
    
    public Timer run() {
        if (length < 0) {
            throw new NullPointerException("There is no length for the timer");
        }
        
        new TimerRunnable(this).runTaskTimerAsynchronously(NexusCore.getPlugin(NexusCore.class), 0L, 1L);
        
        running = true;
        return this;
    }
    
    public int getSecondsElapsed() {
        return toSeconds(getTimeElapsed());
    }
    
    public static int toSeconds(long milliseconds) {
        return (int) Math.floor(milliseconds / 1000.0);
    }
    
    public long getTimeElapsed() {
        return length - time;
    }
    
    public boolean hasElapsed() {
        return hasElapsed(this.length);
    }
    
    public boolean hasElapsed(long length) {
        return getTimeElapsed() >= length;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public int getSecondsLeft() {
        return toSeconds(getTimeLeft());
    }
    
    public long getLength() {
        return length;
    }
    
    public void setLength(long l) {
        this.length = l;
        this.time = l;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
    
    @Override
    public String toString() {
        return "Timer{" +
                ", callback=" + (callback == null ? null : callback.getClass().getName()) +
                ", cancelled=" + cancelled +
                ", length=" + length +
                ", paused=" + paused +
                ", running=" + running +
                ", time=" + time +
                '}';
    }
}
