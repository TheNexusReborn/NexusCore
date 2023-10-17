package com.thenexusreborn.nexuscore.server;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.server.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.ServerProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.lang.reflect.Field;

public class SpigotServerManager extends ServerManager implements Listener {
    private final NexusCore plugin;
    
    public SpigotServerManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        if (plugin.getMotdSupplier() == null) {
            return;
        }
        
        e.setMotd(MCUtils.color(plugin.getMotdSupplier().get()));
        
        int numPlayers = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer == null) {
                continue;
            }
            if (!nexusPlayer.getToggleValue("vanish") && !nexusPlayer.getToggleValue("incognito")) {
                numPlayers++;
            }
        }
        
        if (numPlayers == e.getNumPlayers()) {
            return;
        }

        try {
            Field numPlayersField = ServerListPingEvent.class.getDeclaredField("numPlayers");
            numPlayersField.setAccessible(true);
            numPlayersField.set(e, numPlayers);
        } catch (Exception ex) {
            plugin.getLogger().severe("Could not modify the numPlayers field: " + ex.getClass().getSimpleName());
            ex.printStackTrace();
        }
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
