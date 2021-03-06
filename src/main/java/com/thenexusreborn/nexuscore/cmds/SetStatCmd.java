package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.stats.StatRegistry;
import com.thenexusreborn.api.util.Operator;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.util.*;
import java.util.function.Consumer;

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
    
        Consumer<NexusPlayer> consumer = player -> {
            if (!StatRegistry.isValidStat(args[1])) {
                sender.sendMessage(MCUtils.color("&cCould not find a stat with that name."));
                return;
            }
    
            Number value;
            if (StatRegistry.isIntegerStat(args[1])) {
                try {
                    value = Math.abs(Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(MCUtils.color("&cYou provided an invalid integer value for that stat type."));
                    return;
                }
            } else if (StatRegistry.isDoubleStat(args[1])) {
                try {
                    value = Math.abs(Double.parseDouble(args[2]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(MCUtils.color("&cYou provided an invalid decimal value for that stat type."));
                    return;
                }
            } else {
                sender.sendMessage(MCUtils.color("&cUnhandled stat value type. This is a bug, please report"));
                return;
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
                player.changeStat(args[1], value, operator);
                operator = Operator.ADD;
                value = oldValue;
            }
    
            double currentValue = player.getStatValue(args[1]);
            if (operator.calculate(currentValue, value).doubleValue() < 0) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You cannot modify a stat below 0"));
                return;
            }
    
            player.changeStat(args[1], value, operator);
            sender.sendMessage(MCUtils.color("&eYou modified the stat &b" + args[1] + " &ewith the value &b" + value + " &eand the operation &b" + operator.name().toLowerCase()));
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
