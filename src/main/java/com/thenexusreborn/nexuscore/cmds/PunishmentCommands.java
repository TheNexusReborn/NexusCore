package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starlib.Pair;
import com.stardevllc.starlib.time.TimeParser;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.util.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

public class PunishmentCommands implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public PunishmentCommands(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        PunishmentType type = switch (cmd.getName().toLowerCase()) {
            case "ban", "tempban", "tb" -> PunishmentType.BAN;
            case "mute", "tempmute", "tm" -> PunishmentType.MUTE;
            case "kick" -> PunishmentType.KICK;
            case "warn" -> PunishmentType.WARN;
            case "blacklist" -> PunishmentType.BLACKLIST;
            default -> null;
        };
        
        if (type == null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid punishment type."));
            return true;
        }
        
        long length = -1;
        if (cmd.getName().toLowerCase().contains("temp") || cmd.getName().equalsIgnoreCase("tb") || cmd.getName().equalsIgnoreCase("tm")) {
            if (!(args.length > 2)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "/" + label + " <target> <length> <reason>"));
                return true;
            }
            
            try {
                length = new TimeParser().parseTime(args[1]);
            } catch (Exception e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid length argument."));
                return true;
            }
            
            if (length == 0) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid length argument."));
                return true;
            }
        } else {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "/" + label + " <target> <reason>"));
                return true;
            }
        }
        
        Rank minRank = null;
        if (length == -1) {
            minRank = type.getMinRankPermanent();
        }
        
        if (minRank == null) {
            minRank = type.getMinRankTemporary();
        }
        
        Rank actorRank = MCUtils.getSenderRank(plugin, sender);
        if (actorRank.ordinal() > minRank.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You do not have permission to use that punishment type."));
            return true;
        }
    
        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
        Pair<UUID, String> playerInfo = playerManager.getPlayerFromIdentifier(args[0]);
        if (playerInfo == null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a player with that identifier."));
            return true;
        }
        
        UUID targetUniqueID = playerInfo.firstValue();
        String targetName = playerInfo.secondValue();
        Rank targetRank = playerManager.getPlayerRank(targetUniqueID);

        if (targetRank.ordinal() < actorRank.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You cannot " + type.name().toLowerCase() + " that player because their rank is higher than your own."));
            return true;
        }
        
        String actor;
        if (sender instanceof Player) {
            actor = ((Player) sender).getUniqueId().toString();
        } else {
            actor = sender.getName();
        }
        
        String server = NexusAPI.getApi().getServerManager().getCurrentServer().getName();
        
        StringBuilder sb = new StringBuilder();
        int startIndex = 1;
        if (length > 0) {
            startIndex = 2;
        }

        if (args[startIndex].startsWith("[PMR]")) {
            if (sender instanceof ConsoleCommandSender) {
                actor = "PowerMoveRegulator";
            } else if (actorRank == Rank.NEXUS) {
                actor = "PowerMoveRegulator";
            }
            startIndex++;
        }
        for (int i = startIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        
        String reason = sb.toString().trim();
        Punishment punishment = new Punishment(System.currentTimeMillis(), length, actor, targetUniqueID.toString(), server, reason, type, Visibility.SILENT);
        
        if (punishment.getType() == PunishmentType.WARN) {
            punishment.setAcknowledgeInfo(new AcknowledgeInfo(Utils.generateCode(8, true, true, true)));
        } else if (Stream.of(PunishmentType.BAN, PunishmentType.BLACKLIST, PunishmentType.KICK).anyMatch(punishmentType -> punishment.getType() == punishmentType)) {
            Player targetPlayer = Bukkit.getPlayer(targetUniqueID);
            if (targetPlayer != null) {
                targetPlayer.kickPlayer(punishment.formatKick()); //TODO this doesn't provide an id right away though
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                NexusAPI.getApi().getPrimaryDatabase().save(punishment);
            } catch (SQLException e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not create the punishment. Please report to Firestar311."));
                e.printStackTrace();
                return;
            }

            if (punishment.getId() < 1) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not create the punishment. Please report to Firestar311."));
                plugin.getLogger().severe("Had a Punishment ID that was " + punishment.getId() + " after saving to the database, with no SQL Error");
                return;
            }

            NexusAPI.getApi().getNetworkManager().send("punishment", punishment.getId() + "");
            StaffChat.sendPunishment(punishment); 
        });
        return true;
    }
}
