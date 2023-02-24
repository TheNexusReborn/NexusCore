package com.thenexusreborn.nexuscore.thread;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.clock.Clock;

public class ClockThread extends NexusThread<NexusCore> {
    public ClockThread(NexusCore plugin) {
        super(plugin, 1L, true);
    }
    
    public void onRun() {
        for (Clock<?> clock : Clock.getClocks()) {
            if (clock.isPaused()) {
                continue;
            }
    
            boolean result = clock.callback();
            if (!result) {
                clock.cancel();
            }
            clock.count();
        }
    }
}
