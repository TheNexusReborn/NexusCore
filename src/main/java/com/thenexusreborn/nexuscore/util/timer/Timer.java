package com.thenexusreborn.nexuscore.util.timer;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.ReturnableCallback;
import com.thenexusreborn.nexuscore.util.updater.UpdateType;

import java.util.*;

public class Timer {
    public final Map<UpdateType, Long> lastUpdates = new HashMap<>();
    private final ReturnableCallback<TimerSnapshot, Boolean> callback;
    private boolean cancelled = false;
    private long length = -1;
    private long paused = -1;
    private boolean running = false;
    private long time;
    
    public Timer(ReturnableCallback<TimerSnapshot, Boolean> callback) {
        this.callback = callback;
    }
    
    public static String formatTime(int time) {
        final int minutes = time / 60;
        time -= minutes * 60;
        String s = "";
        if (minutes < 10) {
            s = s + "0" + minutes + "m";
        } else if (minutes >= 10) {
            s = s + minutes + "m";
        }
        if (time < 10 && time >= 0) {
            s = s + "0" + time + "s";
        } else if (time >= 10) {
            s = s + time + "s";
        }
        return s;
    }
    
    public static String formatLongerTime(int time) {
        int minutes = (time / 60);
        time -= minutes * 60;
        int hours = (minutes / 60);
        minutes -= hours * 60;
        return (hours > 0 ? (hours < 10 ? "0" : "") + hours + "h" : "") + (minutes < 10 ? "0" : "") + minutes + "m" + (time < 10 ? "0" : "") + time + "s";
    }
    
    public static String formatTimeShort(long seconds) {
        long mins = seconds / 60L;
        long secs = seconds % 60L;
        String text = "";
        if (mins > 0L) {
            text = text + mins + "m";
        }
        if (secs > 0L) {
            text = text + secs + "s";
        }
        return text;
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
    
    //TODO remove time and length
    
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
                "lastUpdates=" + lastUpdates +
                ", callback=" + ((callback == null) ? null : callback.getClass().getName()) +
                ", cancelled=" + cancelled +
                ", length=" + length +
                ", paused=" + paused +
                ", running=" + running +
                ", time=" + time +
                '}';
    }
}
