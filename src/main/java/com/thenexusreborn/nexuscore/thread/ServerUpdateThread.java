package com.thenexusreborn.nexuscore.thread;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusThread;

public class ServerUpdateThread extends NexusThread<NexusCore> {
    
    public ServerUpdateThread(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
        //TODO
    }
}