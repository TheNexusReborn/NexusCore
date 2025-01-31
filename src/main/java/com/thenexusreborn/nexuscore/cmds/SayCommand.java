package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.colors.StarColors;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SayCommand implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public SayCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.HELPER.ordinal()) {
            sender.sendMessage(StarColors.color("&cYou do not have permission to use that command."));
            return true;
        }
    
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
    
        Bukkit.broadcastMessage(StarColors.color("&8[&f&l&oSAY&8] " + senderRank.getColor() + sender.getName() + "&8: &b" + sb));
        return true;
    }
}
