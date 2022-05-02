package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.data.DataManager;
import com.thenexusreborn.api.tags.TagManager;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.server.SpigotServerManager;
import com.thenexusreborn.nexuscore.thread.SpigotThreadFactory;

import java.sql.*;

public class SpigotNexusAPI extends NexusAPI {
    
    private NexusCore plugin;
    
    public SpigotNexusAPI(NexusCore plugin) {
        super(Environment.valueOf(plugin.getConfig().getString("environment").toUpperCase()), plugin.getLogger(), new DataManager(), new TagManager(), new SpigotPlayerManager(plugin), new SpigotThreadFactory(plugin), new SpigotPlayerFactory(), new SpigotServerManager(plugin));
        this.plugin = plugin;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return plugin.getConnection();
    }
}
