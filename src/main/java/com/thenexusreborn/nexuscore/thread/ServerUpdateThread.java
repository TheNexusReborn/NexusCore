package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.nexuscore.NexusCore;

public class ServerUpdateThread extends StarThread<NexusCore> {
    
    public ServerUpdateThread(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
        //TODO
    }
}