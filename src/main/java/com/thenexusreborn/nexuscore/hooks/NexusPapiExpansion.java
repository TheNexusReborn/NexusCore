package com.thenexusreborn.nexuscore.hooks;

import com.thenexusreborn.nexuscore.NexusCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class NexusPapiExpansion extends PlaceholderExpansion {
    
    private NexusCore plugin;

    public NexusPapiExpansion(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        return "";
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
