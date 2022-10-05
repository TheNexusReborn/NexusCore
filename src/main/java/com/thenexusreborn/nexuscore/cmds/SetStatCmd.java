package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.NexusProfile;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.stats.Stat;
import com.thenexusreborn.api.stats.StatChange;
import com.thenexusreborn.api.stats.StatHelper;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

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
        if (profile != null) return true;

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

        Object value = StatHelper.parseValue(statInfo.getType(), rawValue);
        if (value == null && !(operator == StatOperator.RESET || operator == StatOperator.INVERT)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not parse the value for the stat."));
            return true;
        }

        StatChange statChange = new StatChange(StatHelper.getInfo(statInfo.getName()), profile.getUniqueId(), value, operator, System.currentTimeMillis());
        if (profile instanceof NexusPlayer) {
            profile.getStats().change(statInfo.getName(), value, operator);
        } else {
            NexusAPI.getApi().getPrimaryDatabase().push(statChange);
        }
        sender.sendMessage(MCUtils.color(MsgType.INFO + "You changed the stat with the operation " + operator.name()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
