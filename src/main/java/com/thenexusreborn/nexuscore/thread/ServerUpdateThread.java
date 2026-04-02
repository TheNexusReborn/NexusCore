package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.StarThread;
import com.thenexusreborn.nexuscore.NexusCore;

public class ServerUpdateThread extends StarThread<NexusCore> {
    
    public ServerUpdateThread(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
        //TODO
    }
}