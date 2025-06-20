package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.api.server.ServerType;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerVisibilityThread extends StarThread<NexusCore> {
    
    public PlayerVisibilityThread(NexusCore plugin) {
        super(plugin, 1L, 1L, false);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void onRun() {
        List<NexusServer> servers = new LinkedList<>();
        Iterator<NexusServer> serverIterator = NexusReborn.getServerRegistry().iterator();
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
                    otherPlayer.hidePlayer(plugin, player);
                    continue;
                }

                NexusServer otherPlayerServer = null;

                for (NexusServer nexusServer : servers) {
                    if (nexusServer.getPlayers().contains(otherPlayer.getUniqueId())) {
                        otherPlayerServer = nexusServer;
                        break;
                    }
                }

                NexusPlayer otherNexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(otherPlayer.getUniqueId());
                
                if (otherPlayerServer == null || otherNexusPlayer == null) {
                    player.hidePlayer(plugin, otherPlayer);
                    continue;
                }

                if (!playerServer.getName().equals(otherPlayerServer.getName())) {
                    player.hidePlayer(plugin, otherPlayer);
                    otherPlayer.hidePlayer(plugin, player);
                } else {
                    boolean canSeeOther;
                    try {
                        canSeeOther = playerServer.recalculateVisibility(player.getUniqueId(), otherPlayer.getUniqueId());
                    } catch (UnsupportedOperationException e) {
                        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
                        if (nexusPlayer != null) {
                            Rank playerRank = nexusPlayer.getRank();

                            Rank otherPlayerRank = otherNexusPlayer.getRank();
                            boolean otherPlayerIsVanished = otherPlayerRank.ordinal() <= Rank.HELPER.ordinal() && otherNexusPlayer.getToggleValue("vanish");
                            boolean otherPlayerIsIncognito = otherPlayerRank.ordinal() <= Rank.MEDIA.ordinal() && otherNexusPlayer.getToggleValue("incognito");
                            boolean otherPlayerIsNotVisible = otherPlayerIsVanished || otherPlayerIsIncognito;

                            if (otherPlayerIsNotVisible) {
                                canSeeOther = otherPlayerRank.ordinal() >= playerRank.ordinal();
                            } else {
                                canSeeOther = true;
                            }
                        } else {
                            canSeeOther = false;
                        }
                    }

                    if (canSeeOther) {
                        if (!player.canSee(otherPlayer)) {
                            player.showPlayer(plugin, otherPlayer);
                        }
                    } else {
                        if (player.canSee(otherPlayer)) {
                            player.hidePlayer(plugin, otherPlayer);
                        }
                    }
                }
            }
        }
    }
}