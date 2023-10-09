package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.network.NetworkContext;
import com.thenexusreborn.api.network.cmd.NetworkCommand;
import com.thenexusreborn.api.player.PlayerProxy;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.api.registry.DatabaseRegistry;
import com.thenexusreborn.api.registry.NetworkCommandRegistry;
import com.thenexusreborn.api.registry.StatRegistry;
import com.thenexusreborn.api.registry.ToggleRegistry;
import com.thenexusreborn.api.server.Environment;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.player.SpigotPlayerProxy;
import com.thenexusreborn.nexuscore.server.SpigotServerManager;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import me.firestar311.starlib.spigot.scheduler.SpigotScheduler;
import me.firestar311.starsql.api.objects.SQLDatabase;
import me.firestar311.starsql.h2.H2Database;
import me.firestar311.starsql.h2.H2Properties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class SpigotNexusAPI extends NexusAPI {
    
    private final NexusCore plugin;
    
    public SpigotNexusAPI(NexusCore plugin) {
        super(Environment.valueOf(plugin.getConfig().getString("environment").toUpperCase()), NetworkContext.CLIENT, plugin.getLogger(), new SpigotPlayerManager(plugin), new SpigotScheduler(plugin), new SpigotServerManager(plugin));
        this.plugin = plugin;
        PlayerProxy.setProxyClass(SpigotPlayerProxy.class);
    }
    
    @Override
    public void registerDatabases(DatabaseRegistry registry) {
//        ConfigurationSection databasesSection = plugin.getConfig().getConfigurationSection("databases");
//        if (databasesSection != null) {
//            for (String db : databasesSection.getKeys(false)) {
//                String name;
//                String dbName = databasesSection.getString(db + ".name");
//                if (dbName != null && !dbName.equals("")) {
//                    name = dbName;
//                } else {
//                    name = db;
//                }
//                
//                String host = databasesSection.getString(db + ".host");
//                String user = databasesSection.getString(db + ".user");
//                String password = databasesSection.getString(db + ".password");
//                boolean primary = false;
//                if (databasesSection.contains(db + ".primary")) {
//                    primary = databasesSection.getBoolean(db + ".primary");
//                }
//                SQLDatabase database = new MySQLDatabase(plugin.getLogger(), new MySQLProperties().setDatabaseName(name).setHost(host).setUsername(user).setPassword(password));
//                
//                registry.register(database);
//             }
//        }
        SQLDatabase database = new H2Database(plugin.getLogger(), new H2Properties().setDatabaseName("./" + plugin.getDataFolder() + "/database/nexus").setType("file").setUsername("sa").setCloseOnExit(false));
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
    public void registerNetworkCommands(NetworkCommandRegistry registry) {
        registry.register("punishment", new NetworkCommand("punishment", (cmd, origin, args) -> new BukkitRunnable() {
            public void run() {
                long id = Long.parseLong(args[0]);
                Punishment punishment = null;
                try {
                    punishment = NexusAPI.getApi().getPrimaryDatabase().get(Punishment.class, "id", id).get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.WARN || punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.BLACKLIST) {
                    NexusAPI.getApi().getPunishmentManager().addPunishment(punishment);
        
                    Player player = Bukkit.getPlayer(UUID.fromString(punishment.getTarget()));
                    if (player != null && punishment.isActive()) {
                        if (punishment.getType() == PunishmentType.MUTE) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You have been muted by " + punishment.getActorNameCache() + " for " + punishment.getReason() + ". (" + punishment.formatTimeLeft() + ")"));
                        } else if (punishment.getType() == PunishmentType.WARN) {
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You have been warned by " + punishment.getActorNameCache() + " for " + punishment.getReason() + "."));
                            player.sendMessage(MCUtils.color(MsgType.WARN + "You must type the code " + punishment.getAcknowledgeInfo().getCode() + " in chat before you can speak again."));
                        }
                    }
                }
            }
        }.runTaskAsynchronously(plugin)));
        
        registry.register("punishment", new NetworkCommand("removepunishment", (cmd, origin, args) -> {
            long id = Long.parseLong(args[0]);
            Punishment punishment = NexusAPI.getApi().getPunishmentManager().getPunishment(id);
            if (punishment != null) {
                try {
                    punishment = NexusAPI.getApi().getPrimaryDatabase().get(Punishment.class, "id", id).get(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                NexusAPI.getApi().getPunishmentManager().addPunishment(punishment);
            }
        }));
        
        registry.register("staffchat", new NetworkCommand("staffchat", StaffChat::handleIncoming));
    
        for (NexusSpigotPlugin nexusPlugin : plugin.getNexusPlugins()) {
            nexusPlugin.registerNetworkCommands(registry);
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
