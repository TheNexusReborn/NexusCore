package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.helper.CodeGenerator;
import com.stardevllc.helper.Pair;
import com.stardevllc.colors.StarColors;
import com.stardevllc.time.TimeParser;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IPEntry;
import com.thenexusreborn.api.player.PlayerManager;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.punishment.AcknowledgeInfo;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.api.punishment.Visibility;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@SuppressWarnings("DuplicatedCode")
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
            sender.sendMessage(StarColors.color(MsgType.WARN + "Invalid punishment type."));
            return true;
        }
        
        long length = -1;
        if (cmd.getName().toLowerCase().contains("temp") || cmd.getName().equalsIgnoreCase("tb") || cmd.getName().equalsIgnoreCase("tm")) {
            if (!(args.length > 2)) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "/" + label + " <target> <length> <reason>"));
                return true;
            }
            
            try {
                length = new TimeParser().parseTime(args[1]);
            } catch (Exception e) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "Invalid length argument."));
                return true;
            }
            
            if (length == 0) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "Invalid length argument."));
                return true;
            }
        } else {
            if (!(args.length > 1)) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "/" + label + " <target> <reason>"));
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
        
        Rank actorRank = MCUtils.getSenderRank(sender);
        if (actorRank.ordinal() > minRank.ordinal()) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "You do not have permission to use that punishment type."));
            return true;
        }
    
        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
        Pair<UUID, String> playerInfo = playerManager.getPlayerFromIdentifier(args[0]);
        if (playerInfo == null) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Could not find a player with that identifier."));
            return true;
        }
        
        UUID targetUniqueID = playerInfo.key();
        Rank targetRank = playerManager.getPlayerRank(targetUniqueID);

        if (targetRank.ordinal() < actorRank.ordinal()) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "You cannot " + type.name().toLowerCase() + " that player because their rank is higher than your own."));
            return true;
        }
        
        String actor;
        if (sender instanceof Player) {
            actor = ((Player) sender).getUniqueId().toString();
        } else {
            actor = sender.getName();
        }
        
        String server = "Nexus"; //TODO
        
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

        Player targetPlayer = Bukkit.getPlayer(UUID.fromString(punishment.getTarget()));
        
        if (punishment.getType() == PunishmentType.MUTE) {
            if (targetPlayer != null && punishment.isActive()) {
                if (punishment.getType() == PunishmentType.MUTE) {
                    targetPlayer.sendMessage(StarColors.color(MsgType.WARN + "You have been muted by " + punishment.getActorNameCache() + " for " + punishment.getReason() + ". (" + punishment.formatTimeLeft() + ")"));
                }
            }
        } else if (punishment.getType() == PunishmentType.WARN) {
            punishment.setAcknowledgeInfo(new AcknowledgeInfo(CodeGenerator.generate(8, true, true, true)));
            targetPlayer.sendMessage(StarColors.color(MsgType.WARN + "You have been warned by " + punishment.getActorNameCache() + " for " + punishment.getReason() + "."));
            targetPlayer.sendMessage(StarColors.color(MsgType.WARN + "You must type the code " + punishment.getAcknowledgeInfo().getCode() + " in chat before you can speak again."));
        } else if (Stream.of(PunishmentType.BAN, PunishmentType.BLACKLIST, PunishmentType.KICK).anyMatch(punishmentType -> punishment.getType() == punishmentType)) {
            if (targetPlayer != null) {
                targetPlayer.kickPlayer(StarColors.color(punishment.formatKick())); //TODO this doesn't provide an id right away though
            }

            if (punishment.getType() == PunishmentType.BLACKLIST) {
                if (punishment.isActive()) {
                    Set<IPEntry> playerIps = new HashSet<>();
                    for (IPEntry ipEntry : NexusAPI.getApi().getPlayerManager().getIpHistory()) {
                        if (ipEntry.getUuid().equals(targetPlayer.getUniqueId())) {
                            playerIps.add(ipEntry);
                        }
                    }

                    Set<UUID> alts = new HashSet<>();
                    for (IPEntry playerIp : playerIps) {
                        alts.addAll(NexusAPI.getApi().getPlayerManager().getPlayersByIp(playerIp.getIp()));
                    }

                    String kickMessage = StarColors.color(punishment.formatKick());
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (UUID alt : alts) {
                            Player altPlayer = Bukkit.getPlayer(alt);
                            if (altPlayer != null) {
                                altPlayer.kickPlayer(kickMessage);
                            }
                        }
                    });
                }
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                NexusAPI.getApi().getPrimaryDatabase().save(punishment);
            } catch (SQLException e) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "Could not create the punishment. Please report to Firestar311."));
                e.printStackTrace();
                return;
            }

            if (punishment.getId() < 1) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "Could not create the punishment. Please report to Firestar311."));
                plugin.getLogger().severe("Had a Punishment ID that was " + punishment.getId() + " after saving to the database, with no SQL Error");
                return;
            }

            NexusAPI.getApi().getPunishmentManager().addPunishment(punishment);
            plugin.getPunishmentChannel().sendPunishment(punishment);
        });
        return true;
    }
}
