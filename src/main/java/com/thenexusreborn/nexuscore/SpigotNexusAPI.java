package com.thenexusreborn.nexuscore;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.network.NetworkContext;
import com.thenexusreborn.api.network.cmd.NetworkCommand;
import com.thenexusreborn.api.player.IPEntry;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.PlayerProxy;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.api.registry.DatabaseRegistry;
import com.thenexusreborn.api.registry.NetworkCommandRegistry;
import com.thenexusreborn.api.registry.StatRegistry;
import com.thenexusreborn.api.registry.ToggleRegistry;
import com.thenexusreborn.api.server.Environment;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.data.handlers.PositionHandler;
import com.thenexusreborn.nexuscore.player.SpigotPlayerManager;
import com.thenexusreborn.nexuscore.player.SpigotPlayerProxy;
import com.thenexusreborn.nexuscore.server.SpigotServerManager;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import me.firestar311.starlib.spigot.scheduler.SpigotScheduler;
import me.firestar311.starsql.api.objects.SQLDatabase;
import me.firestar311.starsql.mysql.MySQLDatabase;
import me.firestar311.starsql.mysql.MySQLProperties;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
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
        registry.addTypeHandler(new PositionHandler());
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
                if (punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.WARN) {
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
                } else if (punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.BLACKLIST || punishment.getType() == PunishmentType.KICK) {
                    NexusAPI.getApi().getPunishmentManager().addPunishment(punishment);
                    UUID target = UUID.fromString(punishment.getTarget());
                    Player player = Bukkit.getPlayer(UUID.fromString(punishment.getTarget()));

                    if (punishment.getType() == PunishmentType.BLACKLIST) {
                        if (punishment.isActive()) {
                            Set<IPEntry> playerIps = new HashSet<>();
                            for (IPEntry ipEntry : NexusAPI.getApi().getPlayerManager().getIpHistory()) {
                                if (ipEntry.getUuid().equals(target)) {
                                    playerIps.add(ipEntry);
                                }
                            }

                            Set<UUID> alts = new HashSet<>();
                            for (IPEntry playerIp : playerIps) {
                                alts.addAll(NexusAPI.getApi().getPlayerManager().getPlayersByIp(playerIp.getIp()));
                            }

                            String kickMessage = MCUtils.color(punishment.formatKick());
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                for (UUID alt : alts) {
                                    Player altPlayer = Bukkit.getPlayer(alt);
                                    if (altPlayer != null) {
                                        altPlayer.kickPlayer(kickMessage);
                                    }
                                }
                            });
                        }
                    }
                    
                    if (player != null) {
                        NexusPlayer punishedPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(target);
                        if (punishment.isActive() || punishment.getType() == PunishmentType.KICK) {
                            String disconnectMsg = MCUtils.color(punishment.formatKick());
                            if (punishedPlayer.getRank() == Rank.NEXUS) {
                                punishedPlayer.sendMessage("&6&l>> &cSomeone tried to " + punishment.getType().name().toLowerCase() + " you, but you are immune.");
                            } else {
                                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(disconnectMsg));
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(plugin)));
        
        registry.register("removepunishment", new NetworkCommand("removepunishment", (cmd, origin, args) -> {
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
