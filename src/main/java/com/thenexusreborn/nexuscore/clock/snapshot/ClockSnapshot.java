package com.thenexusreborn.nexuscore.clock.snapshot;

/**
 * A snapshot of a clock. This has copies of the information from the clock and all subclasses should have as well.
 */
public abstract class ClockSnapshot {
    protected final long time;
    protected final boolean paused;
    
    /**
     * Constructs a new snapshot based on clock data
     * @param time The time of the clock
     * @param paused The paused control variable
     */
    public ClockSnapshot(long time, boolean paused) {
        this.time = time;
        this.paused = paused;
    }
    
    /**
     * @return The time of the clock at this snapshot. This does not live update
     */
    public long getTime() {
        return time;
    }
    
    /**
     * @return The paused status of the clock at this snapshot. This does not live update
     */
    public boolean isPaused() {
        return paused;
    }
}
