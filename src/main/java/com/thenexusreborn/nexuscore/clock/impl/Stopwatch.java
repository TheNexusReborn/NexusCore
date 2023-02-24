package com.thenexusreborn.nexuscore.clock.impl;

import com.thenexusreborn.nexuscore.clock.*;
import com.thenexusreborn.nexuscore.clock.callback.*;
import com.thenexusreborn.nexuscore.clock.callback.defaults.CountupCallback;
import com.thenexusreborn.nexuscore.clock.snapshot.StopwatchSnapshot;

/**
 * This is your traditional "count up" clock. This usually starts from 0, but you can start from any length using the constructors<br>
 * You can have this automatically stop by providing the default callback {@link CountupCallback}. This does not output anything though and just handles the logic.
 */
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
