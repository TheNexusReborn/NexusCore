package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.api.server.ServerType;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PlayerVisibilityThread extends StarThread<NexusCore> {
    public PlayerVisibilityThread(NexusCore plugin) {
        super(plugin, 20L, 1L, false);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void onRun() {
        List<NexusServer> servers = new LinkedList<>();
        Iterator<NexusServer> serverIterator = NexusAPI.getApi().getServerRegistry().iterator();
        while (serverIterator.hasNext()) {
            NexusServer ns = serverIterator.next();
            if (ns.getType() == ServerType.INSTANCE) {
                serverIterator.remove();
            } else {
                servers.add(ns);
            }
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusServer playerServer = null;
            
            for (NexusServer nexusServer : servers) {
                if (nexusServer.getPlayers().contains(player.getUniqueId())) {
                    playerServer = nexusServer;
                    break;
                }
            }

            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (otherPlayer.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                
                if (playerServer == null) {
                    otherPlayer.hidePlayer(player);
                    continue;
                }

                NexusServer otherPlayerServer = null;

                for (NexusServer nexusServer : servers) {
                    if (nexusServer.getPlayers().contains(otherPlayer.getUniqueId())) {
                        otherPlayerServer = nexusServer;
                        break;
                    }
                }

                NexusPlayer otherNexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(otherPlayer.getUniqueId());
                
                if (otherPlayerServer == null || otherNexusPlayer == null) {
                    player.hidePlayer(otherPlayer);
                    continue;
                }

                if (!playerServer.getName().equals(otherPlayerServer.getName())) {
                    player.hidePlayer(otherPlayer);
                    otherPlayer.hidePlayer(player);
                } else {
                    if (playerServer.recalculateVisibility(player.getUniqueId(), otherPlayer.getUniqueId())) {
                        continue;
                    }
                    
                    NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
                    Rank playerRank = nexusPlayer.getRank();
                    
                    Rank otherPlayerRank = otherNexusPlayer.getRank();
                    boolean otherPlayerIsVanished = otherPlayerRank.ordinal() <= Rank.HELPER.ordinal() && otherNexusPlayer.getToggleValue("vanish");
                    boolean otherPlayerIsIncognito = otherPlayerRank.ordinal() <= Rank.MEDIA.ordinal() && otherNexusPlayer.getToggleValue("incognito");
                    boolean otherPlayerIsNotVisible = otherPlayerIsVanished || otherPlayerIsIncognito;
                    
                    if (otherPlayerIsNotVisible) {
                        if (otherPlayerRank.ordinal() < playerRank.ordinal()) {
                            player.hidePlayer(otherPlayer);
                        } else {
                            player.showPlayer(otherPlayer);
                        }
                    } else {
                        player.showPlayer(otherPlayer);
                    }
                }
            }
        }
    }
}