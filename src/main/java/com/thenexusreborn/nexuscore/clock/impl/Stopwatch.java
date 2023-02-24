package com.thenexusreborn.nexuscore.clock.impl;

import com.thenexusreborn.nexuscore.clock.*;
import com.thenexusreborn.nexuscore.clock.callback.*;
import com.thenexusreborn.nexuscore.clock.snapshot.StopwatchSnapshot;

public class Stopwatch extends Clock<StopwatchSnapshot> {
    
    public Stopwatch() {}
    
    public Stopwatch(long time) {
        super(time);
    }
    
    public Stopwatch(long time, ClockCallback<StopwatchSnapshot> callback, long interval) {
        super(time, callback, interval);
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
    protected boolean shouldCallback(CallbackHolder<StopwatchSnapshot> holder) {
        if (holder.getLastRun() == 0) {
            return true;
        }
        
        return this.time >= holder.getLastRun() - holder.getInterval();
    }
}
