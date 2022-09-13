package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerUpdateTask extends BukkitRunnable {
    
    private JavaPlugin plugin;
    
    public ServerUpdateTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        NexusAPI.getApi().getServerManager().updateStoredData();
    
        ServerInfo currentServer = NexusAPI.getApi().getServerManager().getCurrentServer();
        currentServer.setStatus("online");
        currentServer.setPlayers(Bukkit.getOnlinePlayers().size());
        NexusAPI.getApi().getPrimaryDatabase().push(currentServer);
    }
    
    public void start() {
        runTaskTimerAsynchronously(plugin, 1L, 20L);
    }
}
