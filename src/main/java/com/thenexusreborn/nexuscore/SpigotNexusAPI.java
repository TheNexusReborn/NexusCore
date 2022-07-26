package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.data.objects.Database;
import com.thenexusreborn.api.network.NetworkContext;
import com.thenexusreborn.api.network.cmd.NetworkCommand;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Preference.Info;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.registry.*;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.api.events.*;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.server.SpigotServerManager;
import com.thenexusreborn.nexuscore.thread.SpigotThreadFactory;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SpigotNexusAPI extends NexusAPI {
    
    private NexusCore plugin;
    
    public SpigotNexusAPI(NexusCore plugin) {
        super(Environment.valueOf(plugin.getConfig().getString("environment").toUpperCase()), NetworkContext.CLIENT, plugin.getLogger(), new SpigotPlayerManager(plugin), new SpigotThreadFactory(plugin), new SpigotPlayerFactory(), new SpigotServerManager(plugin));
        this.plugin = plugin;
    }
    
    @Override
    public void registerDatabases(DatabaseRegistry registry) {
        ConfigurationSection databasesSection = plugin.getConfig().getConfigurationSection("databases");
        if (databasesSection != null) {
            for (String db : databasesSection.getKeys(false)) {
                String name;
                String dbName = databasesSection.getString(db + ".name");
                if (dbName != null && !dbName.equals("")) {
                    name = dbName;
                } else {
                    name = db;
                }
                
                String host = databasesSection.getString(db + ".host");
                String user = databasesSection.getString(db + ".user");
                String password = databasesSection.getString(db + ".password");
                boolean primary = false;
                if (databasesSection.contains(db + ".primary")) {
                    primary = databasesSection.getBoolean(db + ".primary");
                }
                Database database = new Database(name, host, user, password, primary);
                registry.register(database);
             }
        }
    
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
        registry.register(new NetworkCommand("tournament", (cmd, origin, args) -> {
            if (args[0].equalsIgnoreCase("delete")) {
                setTournament(null);

                for (NexusPlayer player : NexusAPI.getApi().getPlayerManager().getPlayers().values()) {
                    player.getStats().values().removeIf(stat -> stat.getName().contains("tournament"));
                    player.getStatChanges().removeIf(statChange -> statChange.getStatName().contains("tournament"));
                }
            } else {
                System.out.println("Received request to update in memory tournament info");
                int id = Integer.parseInt(args[0]);
                System.out.println("Tournament id is " + id);
                Tournament tournament = NexusAPI.getApi().getDataManager().getTournament(id);
                System.out.println("Tournament Info: " + tournament);
                setTournament(tournament);
            }
        }));
    
        registry.register(new NetworkCommand("punishment", (cmd, origin, args) -> new BukkitRunnable() {
            public void run() {
                int id = Integer.parseInt(args[0]);
                Punishment punishment = NexusAPI.getApi().getDataManager().getPunishment(id);
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
        
        registry.register(new NetworkCommand("removepunishment", (cmd, origin, args) -> {
            int id = Integer.parseInt(args[0]);
            Punishment punishment = NexusAPI.getApi().getPunishmentManager().getPunishment(id);
            if (punishment != null) {
                punishment = NexusAPI.getApi().getDataManager().getPunishment(id);
                NexusAPI.getApi().getPunishmentManager().addPunishment(punishment);
            }
        }));
        
        registry.register(new NetworkCommand("staffchat", (StaffChat::handleIncoming)));
    
        for (NexusSpigotPlugin nexusPlugin : plugin.getNexusPlugins()) {
            nexusPlugin.registerNetworkCommands(registry);
        }
    }
    
    @Override
    public void registerPreferences(PreferenceRegistry registry) {
        for (Info info : registry.getObjects()) {
            if (info.getName().equalsIgnoreCase("vanish")) {
                info.setHandler((preference, player, oldValue, newValue) -> Bukkit.getPluginManager().callEvent(new VanishToggleEvent(player, oldValue, newValue)));
            } else if (info.getName().equalsIgnoreCase("incognito")) {
                info.setHandler((preference, player, oldValue, newValue) -> Bukkit.getPluginManager().callEvent(new IncognitoToggleEvent(player, oldValue, newValue)));
            }
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
