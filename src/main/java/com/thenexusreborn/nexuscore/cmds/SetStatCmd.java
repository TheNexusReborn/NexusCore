package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.stats.StatRegistry;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class SetStatCmd implements TabExecutor {
    
    private NexusCore plugin;
    
    public SetStatCmd(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    // /setstat <player> <statName> <value>
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        if (args.length != 3) {
            sender.sendMessage(MCUtils.color("&cUsage: /" + label + " <player> <statName> <value>"));
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
        
        if (!StatRegistry.isValidStat(args[1])) {
            sender.sendMessage(MCUtils.color("&cCould not find a stat with that name."));
            return true;
        }
        
        Number value;
        if (StatRegistry.isIntegerStat(args[1])) {
            try {
                value = Math.abs(Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                sender.sendMessage(MCUtils.color("&cYou provided an invalid integer value for that stat type."));
                return true;
            }
        } else if (StatRegistry.isDoubleStat(args[2])) {
            try {
                value = Math.abs(Double.parseDouble(args[2]));
            } catch (NumberFormatException e) {
                sender.sendMessage(MCUtils.color("&cYou provided an invalid decimal value for that stat type."));
                return true;
            }
        } else {
            sender.sendMessage(MCUtils.color("&cUnhandled stat value type. This is a bug, please report"));
            return true;
        }
    
        boolean clear = false;
        Operator operator;
        if (args[2].startsWith("+")) {
            operator = Operator.ADD;
        } else if (args[2].startsWith("-")) {
            operator = Operator.SUBTRACT;
        } else if (args[2].startsWith("*")) {
            operator = Operator.MULTIPLY;
        } else if (args[2].startsWith("/")) {
            operator = Operator.DIVIDE;
        } else {
            Number oldValue = value;
            operator = Operator.MULTIPLY;
            value = 0;
            player.setStat(args[1], value, operator);
            operator = Operator.ADD;
            value = oldValue;
        }
        
        player.setStat(args[1], value, operator);
        sender.sendMessage(MCUtils.color("&eYou modified the stat &b" + args[1] + " &ewith the value &b" + value + " &eand the operation &b" + operator.name().toLowerCase()));
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
