package com.thenexusreborn.nexuscore.clock.impl;

import com.starmediadev.starlib.TimeUnit;
import com.thenexusreborn.nexuscore.clock.*;
import com.thenexusreborn.nexuscore.clock.snapshot.StopwatchSnapshot;

public class Stopwatch extends Clock<StopwatchSnapshot> {
    
    public Stopwatch() {
    }
    
    public Stopwatch(Options options) {
        super(options);
    }
    
    @Override
    public void count() {
        this.time += 50;
    }
    
    @Override
    public StopwatchSnapshot createSnapshot() {
        return new StopwatchSnapshot(time, paused);
    }
    
    @Override
    protected boolean shouldCallback() {
        if (lastCallback == 0) {
            return true;
        }
        
        return this.time >= lastCallback - callbackInterval;
    }
    
    public static class Options extends Clock.Options<StopwatchSnapshot> {
        @Override
        public Options interval(long callbackInterval) {
            return (Options) super.interval(callbackInterval);
        }
    
        @Override
        public Options interval(long interval, TimeUnit unit) {
            return (Options) super.interval(interval, unit);
        }
    
        @Override
        public Options callback(ClockCallback<StopwatchSnapshot> callback) {
            return (Options) super.callback(callback);
        }
    
        @Override
        public Options time(long time) {
            return (Options) super.time(time);
        }
    }
}
