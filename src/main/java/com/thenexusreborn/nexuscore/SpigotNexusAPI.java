package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.network.NetworkContext;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.server.SpigotServerManager;
import com.thenexusreborn.nexuscore.thread.SpigotThreadFactory;

import java.sql.*;

public class SpigotNexusAPI extends NexusAPI {
    
    private NexusCore plugin;
    
    public SpigotNexusAPI(NexusCore plugin) {
        super(Environment.valueOf(plugin.getConfig().getString("environment").toUpperCase()), NetworkContext.CLIENT, plugin.getLogger(), new SpigotPlayerManager(plugin), new SpigotThreadFactory(plugin), new SpigotPlayerFactory(), new SpigotServerManager(plugin));
        this.plugin = plugin;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return plugin.getConnection();
    }
}
