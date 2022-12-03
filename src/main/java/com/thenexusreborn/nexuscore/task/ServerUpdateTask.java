package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.ServerInfo;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusThread;
import org.bukkit.Bukkit;

public class ServerUpdateTask extends NexusThread<NexusCore> {
    
    public ServerUpdateTask(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
        NexusAPI.getApi().getServerManager().updateStoredData();
    
        ServerInfo currentServer = NexusAPI.getApi().getServerManager().getCurrentServer();
        currentServer.setStatus("online");
        currentServer.setPlayers(Bukkit.getOnlinePlayers().size());
        NexusAPI.getApi().getPrimaryDatabase().push(currentServer);
    }
}