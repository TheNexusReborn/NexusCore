package com.thenexusreborn.nexuscore.clock.impl;

import com.thenexusreborn.nexuscore.clock.Clock;
import com.thenexusreborn.nexuscore.clock.snapshot.StopwatchSnapshot;

public class Stopwatch extends Clock<StopwatchSnapshot> {
    @Override
    public void count() {
        this.time += 50;
    }
    
    @Override
    public StopwatchSnapshot createSnapshot() {
        return new StopwatchSnapshot(time, paused);
    }
}
