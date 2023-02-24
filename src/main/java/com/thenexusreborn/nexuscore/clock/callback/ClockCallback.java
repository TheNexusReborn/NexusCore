package com.thenexusreborn.nexuscore.clock.callback;

import com.thenexusreborn.nexuscore.clock.snapshot.ClockSnapshot;

/**
 * This interface is how you define the logic of a timer and what to do on an interval. You can have multiple callbacks in timers.
 * @param <T> The type of the snapshot for the callback
 */
@FunctionalInterface
public interface ClockCallback<T extends ClockSnapshot> {
    boolean callback(T snapshot);
}
