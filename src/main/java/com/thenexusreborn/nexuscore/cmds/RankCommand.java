package com.thenexusreborn.nexuscore.cmds;

import com.starmediadev.starlib.TimeParser;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;

public class RankCommand implements TabExecutor {

    private final NexusCore plugin;

    public RankCommand(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
            return true;
        }

        if (!(args.length > 2)) {
            sender.sendMessage(MCUtils.color("&cUsage: /rank <player> <add|set|remove> <rank> [length]"));
            return true;
        }

        Rank rank;
        try {
            rank = Rank.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(MCUtils.color("&cYou provided an invalid rank name."));
            return true;
        }

        if (rank == Rank.NEXUS) {
            sender.sendMessage(MCUtils.color("&cThe Nexus Team rank cannot be set with a command."));
            for (Player player : Bukkit.getOnlinePlayers()) {
                NexusPlayer onlineNexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
                if (onlineNexusPlayer.getRank() == Rank.NEXUS) {
                    player.sendMessage(MCUtils.color("&c" + sender.getName() + " tried to set " + args[0] + "'s rank to Nexus Team"));
                }
            }
            return true;
        }

        if (senderRank.ordinal() >= rank.ordinal()) {
            sender.sendMessage(MCUtils.color("&cYou cannot set " + args[0] + "'s rank to " + rank.name() + " as it is equal to or higher than your own."));
            return true;
        }

        long time = -1;
        if (args.length > 3) {
            time = new TimeParser().parseTime(args[3]);
        }

        NexusProfile nexusProfile = SpigotUtils.getProfileFromCommand(sender, args[0]);
        if (nexusProfile == null) {
            return true;
        }

        if (senderRank.ordinal() >= nexusProfile.getRank().ordinal()) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(MCUtils.color("&cYou cannot modify " + nexusProfile.getName() + "'s rank as they have " + nexusProfile.getRank().name() + " and you have " + senderRank.name()));
                return true;
            }
        }

        String rankName = rank.getColor() + "&l" + rank.name();

        long expire = -1;
        if (nexusProfile.hasRank(rank)) {
            long existingTime = System.currentTimeMillis() - nexusProfile.getRanks().getExpire(rank);
            if (existingTime > 0) {
                expire = System.currentTimeMillis() + time + existingTime;
            }
        }

        if (time > 0 && expire == -1) {
            expire = System.currentTimeMillis() + time;
        }

        if (args[1].equalsIgnoreCase("set")) {
            nexusProfile.setRank(rank, expire);
            String message = "&eYou set &b" + nexusProfile.getName() + "'s &erank to " + rankName;
            if (time > -1) {
                message += " &efor &b" + Constants.PUNISHMENT_TIME_FORMAT.format(time);
            }
            sender.sendMessage(MCUtils.color(message));
        } else if (args[1].equalsIgnoreCase("add")) {
            try {
                nexusProfile.setRank(rank, expire);
            } catch (Exception e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem setting the rank: " + e.getMessage()));
                return true;
            }
            String message = "&eYou added the rank " + rankName + " &eto the player &b" + nexusProfile.getName();
            if (time > -1) {
                message += " &efor &b" + Constants.PUNISHMENT_TIME_FORMAT.format(time);
            }
            sender.sendMessage(MCUtils.color(message));
        } else if (args[1].equalsIgnoreCase("remove")) {
            try {
                nexusProfile.setRank(rank, expire);
            } catch (Exception e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem removing the rank: " + e.getMessage()));
                return true;
            }
            sender.sendMessage(MCUtils.color("&eYou removed the rank " + rankName + " &efrom &b" + nexusProfile.getName()));
        }

        NexusAPI.getApi().getNetworkManager().send("updaterank", nexusProfile.getUniqueId().toString(), args[1], rank.name(), expire + "");

        StringBuilder sb = new StringBuilder();
        for (Entry<Rank, Long> entry : nexusProfile.getRanks().findAll().entrySet()) {
            sb.append(entry.getKey().name()).append("=").append(entry.getValue()).append(",");
        }

        String ranks;
        if (sb.length() > 0) {
            ranks = sb.substring(0, sb.toString().length() - 1);
        } else {
            ranks = "";
        }

        NexusAPI.getApi().getThreadFactory().runAsync(() -> {
            try {
                NexusAPI.getApi().getPrimaryDatabase().execute("update players set `ranks`='" + ranks + "' where `uniqueId`='" + nexusProfile.getUniqueId().toString() + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (!nexusProfile.isOnline()) {
                NexusAPI.getApi().getPlayerManager().getPlayers().remove(nexusProfile.getUniqueId());
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
