package com.thenexusreborn.nexuscore.clock.snapshot;

public abstract class ClockSnapshot {
    protected final long time;
    protected final boolean paused;
    
    public ClockSnapshot(long time, boolean paused) {
        this.time = time;
        this.paused = paused;
    }
    
    public long getTime() {
        return time;
    }
    
    public boolean isPaused() {
        return paused;
    }
}
