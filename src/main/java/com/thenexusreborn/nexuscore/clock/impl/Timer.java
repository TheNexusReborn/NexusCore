package com.thenexusreborn.nexuscore.clock.impl;

import com.thenexusreborn.nexuscore.clock.Clock;
import com.thenexusreborn.nexuscore.clock.snapshot.*;

public class Timer extends Clock<TimerSnapshot> {
    
    protected long length;
    
    public Timer(long length) {
        this.length = length;
    }
    
    @Override
    public void count() {
        this.time -= 50;
    }
    
    @Override
    public TimerSnapshot createSnapshot() {
        return new TimerSnapshot(this.time, this.paused, this.length);
    }
}
