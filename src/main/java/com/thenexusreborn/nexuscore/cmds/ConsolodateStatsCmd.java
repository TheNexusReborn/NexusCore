package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.*;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class ConsolodateStatsCmd implements TabExecutor {
    
    private NexusCore plugin;
    
    public ConsolodateStatsCmd(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(MCUtils.color("&cYou do not have permission to usre that command"));
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(MCUtils.color("&cUsage: /consolodatestats <player>"));
            return true;
        }
    
        NexusPlayer player;
        try {
            UUID uuid = UUID.fromString(args[0]);
            player = plugin.getPlayerManager().getNexusPlayer(uuid);
        } catch (Exception e) {
            player = plugin.getPlayerManager().getNexusPlayer(args[0]);
        }
    
        if (player == null) {
            sender.sendMessage(MCUtils.color("&cCould not find a player with that identifier"));
            return true;
        }
        
        player.consolodateStats();
        sender.sendMessage(MCUtils.color("&eYou consolodated stats for &b" + player.getName()));
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
