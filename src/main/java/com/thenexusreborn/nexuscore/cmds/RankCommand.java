package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.color.ColorHandler;
import com.stardevllc.starlib.misc.Pair;
import com.stardevllc.starlib.time.TimeParser;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.PlayerManager;
import com.thenexusreborn.api.player.PlayerRanks;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class RankCommand implements TabExecutor {

    private final NexusCore plugin;

    public RankCommand(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(ColorHandler.getInstance().color("&cYou do not have permission to use that command."));
            return true;
        }

        if (!(args.length > 2)) {
            sender.sendMessage(ColorHandler.getInstance().color("&cUsage: /rank <player> <add|set|remove> <rank> [length]"));
            return true;
        }

        Rank rank;
        try {
            rank = Rank.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ColorHandler.getInstance().color("&cYou provided an invalid rank name."));
            return true;
        }

        if (rank == Rank.NEXUS) {
            if (!(sender instanceof Player player) || !player.getUniqueId().toString().equals("3f7891ce-5a73-4d52-a2ba-299839053fdc")) {
                sender.sendMessage(ColorHandler.getInstance().color("&cYou cannot set " + args[0] + "'s rank to " + Rank.NEXUS.name() + " as it is equal to or higher than your own."));
                return true;
            }
        } else if (senderRank.ordinal() >= rank.ordinal() && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ColorHandler.getInstance().color("&cYou cannot set " + args[0] + "'s rank to " + rank.name() + " as it is equal to or higher than your own."));
            return true;
        }

        long time = -1;
        if (args.length > 3) {
            time = new TimeParser().parseTime(args[3]);
        }

        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
        Pair<UUID, String> playerInfo = playerManager.getPlayerFromIdentifier(args[0]);
        if (playerInfo == null) {
            sender.sendMessage(ColorHandler.getInstance().color(MsgType.WARN + "Could not find a player with that identifier."));
            return true;
        }

        UUID targetUniqueID = playerInfo.key();
        String targetName = playerInfo.value();
        PlayerRanks targetRanks = playerManager.getPlayerRanks(targetUniqueID);

        if (senderRank.ordinal() >= targetRanks.get().ordinal()) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ColorHandler.getInstance().color("&cYou cannot modify " + targetName + "'s rank as they have " + targetRanks.get().name() + " and you have " + senderRank.name()));
                return true;
            }
        }

        String rankName = rank.getColor() + "&l" + rank.name();

        long expire = -1;
        if (targetRanks.contains(rank)) {
            long existingTime = System.currentTimeMillis() - targetRanks.getExpire(rank);
            if (existingTime > 0) {
                expire = System.currentTimeMillis() + time + existingTime;
            }
        }

        if (time > 0 && expire == -1) {
            expire = System.currentTimeMillis() + time;
        }

        if (args[1].equalsIgnoreCase("set")) {
            targetRanks.set(rank, expire);
            String message = "&eYou set &b" + targetName + "'s &erank to " + rankName;
            if (time > -1) {
                message += " &efor &b" + Constants.PUNISHMENT_TIME_FORMAT.format(time);
            }
            sender.sendMessage(ColorHandler.getInstance().color(message));
        } else if (args[1].equalsIgnoreCase("add")) {
            try {
                targetRanks.add(rank, expire);
            } catch (Exception e) {
                sender.sendMessage(ColorHandler.getInstance().color(MsgType.WARN + "There was a problem setting the rank: " + e.getMessage()));
                return true;
            }
            String message = "&eYou added the rank " + rankName + " &eto the player &b" + targetName;
            if (time > -1) {
                message += " &efor &b" + Constants.PUNISHMENT_TIME_FORMAT.format(time);
            }
            sender.sendMessage(ColorHandler.getInstance().color(message));
        } else if (args[1].equalsIgnoreCase("remove")) {
            try {
                targetRanks.remove(rank);
            } catch (Exception e) {
                sender.sendMessage(ColorHandler.getInstance().color(MsgType.WARN + "There was a problem removing the rank: " + e.getMessage()));
                return true;
            }
            sender.sendMessage(ColorHandler.getInstance().color("&eYou removed the rank " + rankName + " &efrom &b" + targetName));
        }

        StringBuilder sb = new StringBuilder();
        for (Entry<Rank, Long> entry : targetRanks.findAll().entrySet()) {
            sb.append(entry.getKey().name()).append("=").append(entry.getValue()).append(",");
        }

        String ranks;
        if (!sb.isEmpty()) {
            ranks = sb.substring(0, sb.toString().length() - 1);
        } else {
            ranks = "";
        }

        NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
            try {
                NexusAPI.getApi().getPrimaryDatabase().execute("update players set `ranks`='" + ranks + "' where `uniqueId`='" + targetUniqueID + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
