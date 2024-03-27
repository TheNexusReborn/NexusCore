package com.thenexusreborn.nexuscore.hooks;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
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

        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (params.equalsIgnoreCase("coloredname")) {
            return nexusPlayer.getColoredName();
        } else if (params.equalsIgnoreCase("displayname")) {
            String tag;
            if (nexusPlayer.hasActiveTag()) {
                tag = " " + nexusPlayer.getActiveTag().getDisplayName();
            } else {
                tag =  "";
            }
            return nexusPlayer.getDisplayName() + tag;
        } else if (params.equalsIgnoreCase("level")) {
            return MCUtils.formatNumber(nexusPlayer.getExperience().getLevel());
        } else if (params.equalsIgnoreCase("chatcolor")) {
            Rank rank = nexusPlayer.getRank();
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
            return NexusAPI.getApi().getServerManager().getCurrentServer().getName();
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
