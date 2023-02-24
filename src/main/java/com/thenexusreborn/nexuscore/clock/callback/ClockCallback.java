package com.thenexusreborn.nexuscore.clock.callback;

import com.thenexusreborn.nexuscore.clock.snapshot.ClockSnapshot;

@FunctionalInterface
public interface ClockCallback<T extends ClockSnapshot> {
    boolean callback(T snapshot);
}
