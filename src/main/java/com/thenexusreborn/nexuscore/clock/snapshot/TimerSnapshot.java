package com.thenexusreborn.nexuscore.clock.snapshot;

public class TimerSnapshot extends ClockSnapshot {
    
    protected final long length;
    
    public TimerSnapshot(long time, boolean paused, long length) {
        super(time, paused);
        this.length = length;
    }
    
    public long getLength() {
        return length;
    }
}