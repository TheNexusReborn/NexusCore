package com.thenexusreborn.nexuscore.clock.callback.defaults;

import com.thenexusreborn.nexuscore.clock.callback.ClockCallback;
import com.thenexusreborn.nexuscore.clock.snapshot.StopwatchSnapshot;

/**
 * A default callback to automatically stop a Stopwatch when it reaches a certain time.
 */
public class CountupCallback implements ClockCallback<StopwatchSnapshot> {
    
    private long target;
    
    public CountupCallback(long target) {
        this.target = target;
    }
    
    @Override
    public boolean callback(StopwatchSnapshot snapshot) {
        return snapshot.getTime() < target;
    }
}