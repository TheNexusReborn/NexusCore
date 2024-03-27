package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starmclib.StarThread;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.Bukkit;

public class ServerUpdateThread extends StarThread<NexusCore> {
    
    public ServerUpdateThread(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
        NexusAPI.getApi().getServerManager().updateStoredData();
    
        ServerInfo currentServer = NexusAPI.getApi().getServerManager().getCurrentServer();
        currentServer.setStatus("online");
        currentServer.setPlayers(Bukkit.getOnlinePlayers().size());
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(currentServer);
    }
}