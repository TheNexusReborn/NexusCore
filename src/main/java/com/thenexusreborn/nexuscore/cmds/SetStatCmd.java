package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.frameworks.value.ValueCodec;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.stats.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.util.List;

public class SetStatCmd implements TabExecutor {

    private final NexusCore plugin;

    public SetStatCmd(NexusCore plugin) {
        this.plugin = plugin;
    }

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

        NexusProfile profile = SpigotUtils.getProfileFromCommand(sender, args[0]);
        if (profile == null) {
            return true;
        }

        Stat.Info statInfo = StatHelper.getInfo(args[1]);
        if (statInfo == null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "A stat by that name does not exist."));
            return true;
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

        if (!statInfo.getType().isAllowedOperator(operator)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You have an invalid operation for stat " + statInfo.getName()));
            return true;
        }

        Object value = new ValueCodec().decode(rawValue);
        if (value == null && !(operator == StatOperator.RESET || operator == StatOperator.INVERT)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not parse the value for the stat."));
            return true;
        }

        StatChange statChange = new StatChange(StatHelper.getInfo(statInfo.getName()), profile.getUniqueId(), value, operator, System.currentTimeMillis());
        
        profile.changeStat(statInfo.getName(), value, operator);
    
        NexusAPI.getApi().getNetworkManager().send("updatestat", profile.getUniqueId().toString(), statInfo.getName(), operator.name(), value.toString());
        
        sender.sendMessage(MCUtils.color(MsgType.INFO + "You changed the stat with the operation " + operator.name()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
