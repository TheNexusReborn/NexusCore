package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.stats.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.util.*;
import java.util.function.Consumer;

public class SetStatCmd implements TabExecutor {
    
    private final NexusCore plugin;
    
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
        
        Consumer<NexusPlayer> consumer = player -> {
            Stat stat = player.getStat(args[1]);
            if (stat == null) {
                Stat.Info info = StatHelper.getInfo(args[1]);
                if (info == null) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "A stat by that name does not exist: " + args[1]));
                    return;
                }
                stat = new Stat(info, player.getUniqueId(), System.currentTimeMillis());
            }
            
            StatOperator operator;
            String rawValue;
            if (args[2].startsWith("+")) {
                operator = StatOperator.ADD;
                rawValue = args[2].substring(1);
            } else if (args[2].startsWith("-")) {
                rawValue = args[2].substring(1);
                operator = StatOperator.SUBTRACT;
            } else if (args[2].startsWith("*")) {
                operator = StatOperator.MULTIPLY;
                rawValue = args[2].substring(1);
            } else if (args[2].startsWith("/")) {
                operator = StatOperator.DIVIDE;
                rawValue = args[2].substring(1);
            } else if (args[2].equalsIgnoreCase("reset")) {
                operator = StatOperator.RESET;
                rawValue = null;
            } else if (args[2].equalsIgnoreCase("invert")) {
                operator = StatOperator.INVERT;
                rawValue = null;
            } else {
                operator = StatOperator.SET;
                rawValue = args[2];
            }
            
            if (!stat.getType().isAllowedOperator(operator)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You have an invalid operation for stat " + stat.getName()));
                return;
            }
            
            Object value = StatHelper.parseValue(stat.getType(), rawValue);
            if (value == null && !(operator == StatOperator.RESET || operator == StatOperator.INVERT)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not parse the value for the stat."));
                return;
            }
            
            StatChange statChange = new StatChange(StatHelper.getInfo(stat.getName()), stat.getUuid(), value, operator, System.currentTimeMillis());
            StatHelper.changeStat(stat, operator, value);
            NexusAPI.getApi().getPrimaryDatabase().push(statChange);
            sender.sendMessage(MCUtils.color(MsgType.INFO + "You changed the stat with the operation " + operator.name()));
        };
        
        try {
            UUID uuid = UUID.fromString(args[0]);
            NexusAPI.getApi().getPlayerManager().getNexusPlayerAsync(uuid, consumer);
        } catch (Exception e) {
            NexusAPI.getApi().getPlayerManager().getNexusPlayerAsync(args[0], consumer);
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
