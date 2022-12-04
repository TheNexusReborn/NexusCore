package com.thenexusreborn.nexuscore.clock.impl;

import com.starmediadev.starlib.TimeUnit;
import com.thenexusreborn.nexuscore.clock.Clock;
import com.thenexusreborn.nexuscore.clock.snapshot.*;

public class Timer extends Clock<TimerSnapshot> {
    
    protected long length;
    
    public Timer(long length) {
        this.length = length;
        this.time = length;
    }
    
    public Timer(long length, TimeUnit timeUnit) {
        this(timeUnit.toMilliseconds(length));
    }
    
    public long getLength() {
        return length;
    }
    
    public void reset() {
        this.time = length;
    }
    
    public void setLength(long length) {
        long elapsed = this.length - this.time;
        this.length = length;
        this.time = Math.max(this.length - elapsed, 0);
    }
    
    public void addLength(long length) {
        setLength(this.length + length);
    }
    
    public void removeLength(long length) {
        if (length - this.length < 0) {
            length = this.length;
        }
        
        setLength(this.length - length);
    }
    
    @Override
    public Timer start() {
        return (Timer) super.start();
    }
    
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
}
