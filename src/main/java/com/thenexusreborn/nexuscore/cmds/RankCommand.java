package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.api.helper.TimeHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

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
            time = TimeHelper.parseTime(args[3]);
        }
    
        long finalTime = time;
        Consumer<NexusPlayer> consumer = nexusPlayer -> {
            if (senderRank.ordinal() >= nexusPlayer.getRank().ordinal()) {
                if (!(sender instanceof ConsoleCommandSender)) {
                    sender.sendMessage(MCUtils.color("&cYou cannot modify " + nexusPlayer.getName() + "'s rank as they have " + nexusPlayer.getRank().name() + " and you have " + senderRank.name()));
                    return;
                }
            }
            
            String rankName = rank.getColor() + "&l" + rank.name();
    
            long expire = -1;
            if (nexusPlayer.getRanks().containsKey(rank)) {
                long existingTime = System.currentTimeMillis() - nexusPlayer.getRanks().get(rank);
                if (existingTime > 0) {
                    expire = System.currentTimeMillis() + finalTime + existingTime;
                }
            }
            
            if (finalTime > 0 && expire == -1) {
                expire = System.currentTimeMillis() + finalTime;
            }
            
            if (args[1].equalsIgnoreCase("set")) {
                nexusPlayer.getRanks().clear();
                try {
                    nexusPlayer.setRank(rank, expire);
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem setting the rank: " + e.getMessage()));
                    return;
                }
                String message = "&eYou set &b" + nexusPlayer.getName() + "'s &erank to " + rankName;
                if (finalTime > -1) {
                    message += " &efor &b" + TimeHelper.formatTime(finalTime);
                }
                sender.sendMessage(MCUtils.color(message));
            } else if (args[1].equalsIgnoreCase("add")) {
                try {
                    nexusPlayer.setRank(rank, expire);
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem setting the rank: " + e.getMessage()));
                    return;
                }
                String message = "&eYou added the rank " + rankName + " &eto the player &b" + nexusPlayer.getName();
                if (finalTime > -1) {
                    message += " &efor &b" + TimeHelper.formatTime(finalTime);
                }
                sender.sendMessage(MCUtils.color(message));
            } else if (args[1].equalsIgnoreCase("remove")) {
                try {
                    nexusPlayer.removeRank(rank);
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem removing the rank: " + e.getMessage()));
                    return;
                }
                sender.sendMessage(MCUtils.color("&eYou removed the rank " + rankName + " &efrom &b" + nexusPlayer.getName()));
            }
            
            NexusAPI.getApi().getNetworkManager().send("updaterank", nexusPlayer.getUniqueId().toString(), args[1], rank.name(), expire + "");
    
            StringBuilder sb = new StringBuilder();
            for (Entry<Rank, Long> entry : nexusPlayer.getRanks().entrySet()) {
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
                    NexusAPI.getApi().getPrimaryDatabase().execute("update players set `ranks`='" + ranks + "' where `uniqueId`='" + nexusPlayer.getUniqueId().toString() + "';");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (!nexusPlayer.isOnline()) {
                    NexusAPI.getApi().getPlayerManager().getPlayers().remove(nexusPlayer.getUniqueId());
                }
            });
        };
        
        try {
            NexusAPI.getApi().getPlayerManager().getNexusPlayerAsync(UUID.fromString(args[0]), consumer);
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
