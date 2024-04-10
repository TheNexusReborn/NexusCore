package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NexusVersionCmd implements CommandExecutor {
    private NexusCore plugin;

    public NexusVersionCmd(NexusCore plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ColorUtils.color("&6&l>> &e&lThe Nexus Reborn Plugin Versions"));
        sender.sendMessage(ColorUtils.color("&6&l>> &eNexusAPI v" + NexusAPI.getApi().getVersion().toString()));
        NexusCore nexusCore = (NexusCore) Bukkit.getServer().getPluginManager().getPlugin("NexusCore");
        sender.sendMessage(ColorUtils.color("&6&l>> &eNexusCore v" + nexusCore.getDescription().getVersion()));
        for (NexusSpigotPlugin nexusPlugin : nexusCore.getNexusPlugins()) {
            sender.sendMessage(ColorUtils.color("&6&l>> &e" + nexusPlugin.getName() + " v" + nexusPlugin.getDescription().getVersion()));
        }

        return true;
    }
}
