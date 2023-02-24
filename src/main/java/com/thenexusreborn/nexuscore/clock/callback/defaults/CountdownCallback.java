package com.thenexusreborn.nexuscore.clock.callback.defaults;

import com.thenexusreborn.nexuscore.clock.callback.ClockCallback;
import com.thenexusreborn.nexuscore.clock.snapshot.TimerSnapshot;

public class CountdownCallback implements ClockCallback<TimerSnapshot> {
    @Override
    public boolean callback(TimerSnapshot snapshot) {
        return snapshot.getTime() > 0;
    }
}
