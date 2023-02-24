package com.thenexusreborn.nexuscore.clock.impl;

import com.thenexusreborn.nexuscore.clock.Clock;
import com.thenexusreborn.nexuscore.clock.callback.*;
import com.thenexusreborn.nexuscore.clock.callback.defaults.CountdownCallback;
import com.thenexusreborn.nexuscore.clock.snapshot.TimerSnapshot;

/**
 * This is your traditional "countdown" timer. You must supply a starting length. <br>
 * By default, this will not auto-cancel without a callback telling it to do so. There is a default callback for this: {@link CountdownCallback}. This has no output, just logic
 */
public class Timer extends Clock<TimerSnapshot> {
    
    /**
     * The length of the timer.
     */
    protected long length;
    
    /**
     * Constructs a new timer based on this length
     * @param length The length in millseconds
     */
    public Timer(long length) {
        super(length);
        this.length = length;
    }
    
    /**
     * Constructs a new timer based on a length and a callback
     * @param length The length in millseconds
     * @param callback The callback
     * @param interval The interval in millseconds
     */
    public Timer(long length, ClockCallback<TimerSnapshot> callback, long interval) {
        super(length, callback, interval);
        this.length = length;
    }
    
    /**
     * @return The length of the timer
     */
    public long getLength() {
        return length;
    }
    
    /**
     * Resets the current time back to the length
     */
    public void reset() {
        this.time = length;
    }
    
    /**
     * Sets the length of the timer. This will take into account of elapsed time already and change it accordingly.
     * @param length The new length of the timer in milliseconds
     */
    public void setLength(long length) {
        long elapsed = this.length - this.time;
        this.length = length;
        this.time = Math.max(this.length - elapsed, 0);
    }
    
    /**
     * Adds length to this timer. See the setLength() method
     * @param length The milliseconds to add
     */
    public void addLength(long length) {
        setLength(this.length + length);
    }
    
    /**
     * Removes length from this timer. See the setLength() method<br>
     * This will not let it go below 0
     * @param length The milliseconds to remove
     */
    public void removeLength(long length) {
        if (length - this.length < 0) {
            length = this.length;
        }
        
        setLength(this.length - length);
    }
    
    /**
     * This does nothing different than cast to the Timer class than the super start method. This allows you to create and start a timer in the same line
     * @return The timer instance
     */
    @Override
    public Timer start() {
        return (Timer) super.start();
    }
    
    /**
     * Counts this timer down
     */
    @Override
    public void count() {
        if (time >= 50) {
            this.time -= 50;
        }
    }
    
    @Override
    public TimerSnapshot createSnapshot() {
        return new TimerSnapshot(this.time, this.paused, this.length);
    }
    
    @Override
    protected boolean shouldCallback(CallbackHolder<TimerSnapshot> holder) {
        if (holder.getLastRun() == 0) {
            return true;
        }
        
        return this.time <= holder.getLastRun() - holder.getInterval();
    }
}
