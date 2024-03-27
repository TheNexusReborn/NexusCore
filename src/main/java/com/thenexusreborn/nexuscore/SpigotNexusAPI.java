package com.thenexusreborn.nexuscore;

import com.stardevllc.starmclib.task.SpigotTaskFactory;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.PlayerProxy;
import com.thenexusreborn.api.registry.StatRegistry;
import com.thenexusreborn.api.registry.ToggleRegistry;
import com.thenexusreborn.api.server.Environment;
import com.thenexusreborn.api.sql.DatabaseRegistry;
import com.thenexusreborn.api.sql.mysql.MySQLDatabase;
import com.thenexusreborn.api.sql.mysql.MySQLProperties;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.player.SpigotPlayerProxy;
import com.thenexusreborn.nexuscore.server.SpigotServerManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SpigotNexusAPI extends NexusAPI {
    
    private final NexusCore plugin;
    
    public SpigotNexusAPI(NexusCore plugin) {
        super(Environment.valueOf(plugin.getConfig().getString("environment").toUpperCase()), plugin.getLogger(), new SpigotPlayerManager(plugin), new SpigotTaskFactory(plugin), new SpigotServerManager(plugin));
        this.plugin = plugin;
        PlayerProxy.setProxyClass(SpigotPlayerProxy.class);
    }
    
    @Override
    public void registerDatabases(DatabaseRegistry registry) {
        FileConfiguration config = plugin.getConfig();
        SQLDatabase database = new MySQLDatabase(plugin.getLogger(), new MySQLProperties().setDatabaseName(config.getString("databases.database.database")).setHost(config.getString("databases.database.host")).setUsername(config.getString("databases.database.username")).setPassword(config.getString("databases.database.password")));
        registry.register(database);
    
        for (NexusSpigotPlugin nexusPlugin : plugin.getNexusPlugins()) {
            nexusPlugin.registerDatabases(registry);
        }
    }
    
    @Override
    public void registerStats(StatRegistry registry) {
        for (NexusSpigotPlugin nexusPlugin : plugin.getNexusPlugins()) {
            nexusPlugin.registerStats(registry);
        }
    }
    
    @Override
    public void registerToggles(ToggleRegistry registry) {
        for (NexusSpigotPlugin nexusPlugin : plugin.getNexusPlugins()) {
            nexusPlugin.registerToggles(registry);
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return plugin.getConnection();
    }
    
    @Override
    public File getFolder() {
        return plugin.getDataFolder();
    }
}
