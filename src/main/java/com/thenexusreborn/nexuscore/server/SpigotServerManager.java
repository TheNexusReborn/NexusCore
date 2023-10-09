package com.thenexusreborn.nexuscore.server;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.server.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import org.bukkit.Bukkit;

public class SpigotServerManager extends ServerManager {
    
    private final NexusCore plugin;
    
    public SpigotServerManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void setupCurrentServer() {
        int multicraftId = plugin.getConfig().getInt("serverInfo.multicraftid");
        String ip = ServerProperties.getServerIp();
        String name = plugin.getConfig().getString("serverInfo.serverName");
        int port = ServerProperties.getServerPort();
        int players = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        int hiddenPlayers = 0;
        String type = plugin.getConfig().getString("serverInfo.type");
        String status = "loading";
        String state = "none";
        long id = plugin.getConfig().getLong("serverInfo.id");
        this.currentServer = new ServerInfo(multicraftId, ip, name, port, players, maxPlayers, hiddenPlayers, type, status, state);
        this.currentServer.setId(id);
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(this.currentServer);
        plugin.getConfig().set("serverInfo.id", this.currentServer.getId());
        plugin.saveConfig();
    }
}
