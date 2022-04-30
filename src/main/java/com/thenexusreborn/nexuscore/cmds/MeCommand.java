package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.*;

public class MeCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(MCUtils.color("&cNo"));
        return true;
    }
}
