package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.color.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MeCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ColorUtils.color("&cNo"));
        return true;
    }
}
