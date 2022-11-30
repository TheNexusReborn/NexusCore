package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.nexuscore.clock.Clock;

public class ClockTask extends NexusTask<NexusCore> {
    public ClockTask(NexusCore plugin) {
        super(plugin, 1L, true);
    }
    
    public void onRun() {
        for (Clock<?> clock : Clock.getClocks()) {
            if (clock.isPaused()) {
                continue;
            }
    
            clock.count();
            boolean result = clock.callback();
            if (!result) {
                clock.cancel();
            }
        }
    }
}
