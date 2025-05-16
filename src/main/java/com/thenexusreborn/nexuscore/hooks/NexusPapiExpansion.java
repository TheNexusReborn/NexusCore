package com.thenexusreborn.nexuscore.hooks;

import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class NexusPapiExpansion extends PlaceholderExpansion {
    
    private NexusCore plugin;

    public NexusPapiExpansion(NexusCore plugin) {
        this.plugin = plugin;
    }

    /* 
    nexuscore_coloredname - Colored name of the player
    nexuscore_displayname - Main Displayname including prefix, name and tag
    nexuscore_level - Player level
    nexuscore_chatcolor - Player's chat color based on rank
    nexuscore_servername - Name of the current server
     */
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return null;
        }

        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (params.startsWith("coloredname")) {
            if (params.contains("_true")) {
                return nexusPlayer.getTrueColoredName();
            }
            
            return nexusPlayer.getColoredName();
        } else if (params.startsWith("displayname")) {
            String tag;
            if (nexusPlayer.hasActiveTag()) {
                tag = " " + nexusPlayer.getActiveTag().getDisplayName();
            } else {
                tag =  "";
            }
            
            String displayName;
            
            if (params.contains("_true")) {
                displayName = nexusPlayer.getTrueDisplayName();
            } else {
                displayName = nexusPlayer.getDisplayName();
            }
            
            return displayName + tag;
        } else if (params.startsWith("level")) {
            int level;
            if (params.contains("_true")) {
                level = nexusPlayer.getTrueExperience().getLevel();
            } else {
                level = nexusPlayer.getExperience().getLevel();
            }
            
            return MCUtils.formatNumber(level);
        } else if (params.startsWith("chatcolor")) {
            Rank rank;
            
            if (params.contains("_true")) {
                rank = nexusPlayer.getRank();
            } else {
                rank = nexusPlayer.getEffectiveRank();
            }
            
            if (rank == Rank.NEXUS) {
                return "&6";
            } else if (rank.ordinal() >= Rank.ADMIN.ordinal() && rank.ordinal() <= Rank.HELPER.ordinal()) {
                return "&b";
            } else if (rank.ordinal() >= Rank.VIP.ordinal() && rank.ordinal() <= Rank.MEDIA.ordinal()) {
                return "&d";
            } else if (rank.ordinal() >= Rank.PLATINUM.ordinal() && rank.ordinal() <= Rank.DIAMOND.ordinal()) {
                return "&3";
            } else if (rank.ordinal() >= Rank.BRASS.ordinal() && rank.ordinal() <= Rank.IRON.ordinal()) {
                return "&f";
            } else {
                return "&7";
            }
        } else if (params.equalsIgnoreCase("servername")) {
            for (NexusServer server : NexusReborn.getServerRegistry()) {
                if (server.getPlayers().contains(player.getUniqueId())) {
                    return server.getName();
                }
            }
            return "Nexus";
        }

        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "nexuscore";
    }

    @Override
    public String getAuthor() {
        return "Firestar311";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
