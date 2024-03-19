package com.thenexusreborn.nexuscore.hooks;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class NexusPapiExpansion extends PlaceholderExpansion {
    
    private NexusCore plugin;

    public NexusPapiExpansion(NexusCore plugin) {
        this.plugin = plugin;
    }

    /* 
    %nexuscore_coloredname% - Colored name of the player
    %nexuscore_displayname% - Main Displayname including rank prefix, name and tag suffix
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
            return nexusPlayer.getDisplayName();
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
