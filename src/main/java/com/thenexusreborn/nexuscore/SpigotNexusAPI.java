package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.data.DataManager;
import com.thenexusreborn.api.tags.TagManager;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.thread.SpigotThreadFactory;

import java.sql.*;

public class SpigotNexusAPI extends NexusAPI {
    
    private NexusCore plugin;
    
    public SpigotNexusAPI(NexusCore plugin) {
        super(plugin.getLogger(), new DataManager(), new TagManager(), new SpigotPlayerManager(plugin), new SpigotThreadFactory(plugin), new SpigotPlayerFactory());
        this.plugin = plugin;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return plugin.getConnection();
    }
}
