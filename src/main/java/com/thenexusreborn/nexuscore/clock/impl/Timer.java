package com.thenexusreborn.nexuscore.clock.impl;

import com.thenexusreborn.nexuscore.clock.*;
import com.thenexusreborn.nexuscore.clock.callback.*;
import com.thenexusreborn.nexuscore.clock.snapshot.TimerSnapshot;

public class Timer extends Clock<TimerSnapshot> {
    
    protected long length;
    
    public Timer(long length) {
        super(length);
        this.length = length;
    }
    
    public Timer(long length, ClockCallback<TimerSnapshot> callback, long interval) {
        super(length, callback, interval);
        this.length = length;
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
