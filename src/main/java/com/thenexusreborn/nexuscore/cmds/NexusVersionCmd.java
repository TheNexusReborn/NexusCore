package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusSpigotPlugin;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class NexusVersionCmd extends NexusCommand<NexusCore> {
    public NexusVersionCmd(NexusCore plugin) {
        super(plugin, "nexusversion", "", Rank.MEMBER);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        sender.sendMessage(StarColors.color("&6&l>> &e&lThe Nexus Reborn Plugin Versions"));
        sender.sendMessage(StarColors.color("&6&l>> &eNexusAPI v" + NexusReborn.getVersion().toString()));
        NexusCore nexusCore = (NexusCore) Bukkit.getServer().getPluginManager().getPlugin("NexusCore");
        sender.sendMessage(StarColors.color("&6&l>> &eNexusCore v" + nexusCore.getDescription().getVersion()));
        for (NexusSpigotPlugin nexusPlugin : nexusCore.getNexusPlugins()) {
            sender.sendMessage(StarColors.color("&6&l>> &e" + nexusPlugin.getName() + " v" + nexusPlugin.getDescription().getVersion()));
        }

        return true;
    }
}