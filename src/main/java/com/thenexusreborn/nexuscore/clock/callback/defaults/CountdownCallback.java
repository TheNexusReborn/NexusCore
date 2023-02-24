package com.thenexusreborn.nexuscore.clock.callback.defaults;

import com.thenexusreborn.nexuscore.clock.callback.ClockCallback;
import com.thenexusreborn.nexuscore.clock.snapshot.TimerSnapshot;

/**
 * A default callback to stop a Timer (or a subclass of Timer) when the time hits 0
 */
public class CountdownCallback implements ClockCallback<TimerSnapshot> {
    @Override
    public boolean callback(TimerSnapshot snapshot) {
        return snapshot.getTime() > 0;
    }
}
