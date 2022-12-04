package com.thenexusreborn.nexuscore.clock.impl;

import com.starmediadev.starlib.TimeUnit;
import com.thenexusreborn.nexuscore.clock.*;
import com.thenexusreborn.nexuscore.clock.snapshot.TimerSnapshot;

public class Timer extends Clock<TimerSnapshot> {
    
    protected long length;
    
    public Timer(long length) {
        this.length = length;
        this.time = length;
    }
    
    public Timer(long length, TimeUnit timeUnit) {
        this(timeUnit.toMilliseconds(length));
    }
    
    public Timer(Options options) {
        super(options);
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
    
    @Override
    protected boolean shouldCallback() {
        if (lastCallback == 0) {
            return true;
        }
        
        return this.time <= lastCallback - callbackInterval;
    }
    
    public static class Options extends Clock.Options<TimerSnapshot> {
        private long length;
        
        public Options length(long length) {
            this.length = length;
            this.time(length);
            return this;
        }
    
        public Options length(long length, TimeUnit unit) {
            return length(unit.toMilliseconds(length));
        }
    
        @Override
        public Options interval(long callbackInterval) {
            return (Options) super.interval(callbackInterval);
        }
    
        @Override
        public Options interval(long interval, TimeUnit unit) {
            return (Options) super.interval(interval, unit);
        }
    
        @Override
        public Options callback(ClockCallback<TimerSnapshot> callback) {
            return (Options) super.callback(callback);
        }
    
        @Override
        public Options time(long time) {
            super.time(time);
            this.length = time;
            return this;
        }
    }
}
