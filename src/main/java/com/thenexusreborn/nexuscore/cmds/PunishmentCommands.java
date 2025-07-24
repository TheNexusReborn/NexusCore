package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starlib.helper.CodeGenerator;
import com.stardevllc.starlib.helper.Pair;
import com.stardevllc.starlib.time.TimeParser;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.ToggleChangeEvent;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
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
        
        PlayerManager playerManager = NexusReborn.getPlayerManager();
        
        NexusPlayer target = null;
        
        //First get the player if they are on the server
        //This includes nicked players
        //This will have to be reworked when multi-servers are back
        Player targetPlayer = getPlayer(args[0]);
        if (targetPlayer != null) {
            target = playerManager.getNexusPlayer(targetPlayer.getUniqueId());
        } else {
            Pair<UUID, String> info = playerManager.getPlayerFromIdentifier(args[0]);
            if (info != null) {
                target = playerManager.getNexusPlayer(info.key());
                if (target == null) {
                    target = playerManager.createPlayerData(info.key(), info.value());
                }
            }
        }
        
        if (target == null) {
            MsgType.WARN.send(sender, "Could not find a player with the identifier %v.", args[0]);
            return true;
        }
        
        if (!target.isNicked()) {
            if (target.getRank().ordinal() < actorRank.ordinal()) {
                MsgType.WARN.send(sender, "You cannot %v %v because their rank is equal to or higher than your own.", type.name().toLowerCase(), target.getName());
                return true;
            }
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
        for (int i = startIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        
        String reason = sb.toString().trim();
        if (reason.startsWith("[PMR]")) {
            if (sender instanceof ConsoleCommandSender) {
                actor = "PowerMoveRegulator";
            } else if (actorRank == Rank.NEXUS) {
                actor = "PowerMoveRegulator";
            }
            reason.replace("[PMR] ", "");
        }
        
        Punishment punishment = new Punishment(System.currentTimeMillis(), length, actor, target.getUniqueId().toString(), server, reason, type, Visibility.SILENT);

        if (punishment.getType() == PunishmentType.MUTE) {
            if (targetPlayer != null && punishment.isActive()) {
                if (punishment.getType() == PunishmentType.MUTE) {
                    targetPlayer.sendMessage(StarColors.color(MsgType.WARN + "You have been muted by " + punishment.getActorNameCache() + " for " + punishment.getReason() + ". (" + punishment.formatTimeLeft() + ")"));
                }
            }
        } else if (punishment.getType() == PunishmentType.WARN) {
            punishment.setAcknowledgeInfo(new AcknowledgeInfo(CodeGenerator.generateAllOptions(8)));
            targetPlayer.sendMessage(StarColors.color(MsgType.WARN + "You have been warned by " + punishment.getActorNameCache() + " for " + punishment.getReason() + "."));
            targetPlayer.sendMessage(StarColors.color(MsgType.WARN + "You must type the code " + punishment.getAcknowledgeInfo().getCode() + " in chat before you can speak again."));
        } else if (Stream.of(PunishmentType.BAN, PunishmentType.BLACKLIST, PunishmentType.KICK).anyMatch(punishmentType -> punishment.getType() == punishmentType)) {
            if (targetPlayer != null) {
                if (target.isNicked() && target.getRank().ordinal() <= actorRank.ordinal()) {
                    Toggle vanish = target.getToggle("vanish");
                    ToggleChangeEvent changeEvent = new ToggleChangeEvent(target, vanish, vanish.getValue(), !vanish.getValue());
                    Bukkit.getPluginManager().callEvent(changeEvent);
                    vanish.setValue(!vanish.getValue());
                    NexusReborn.getPrimaryDatabase().saveSilent(vanish);
                    target.sendMessage(MsgType.INFO.format("Your nickname was " + punishment.getType().getVerb() + " by " + sender.getName() + ", you have been put into vanish mode."));
                } else {
                    targetPlayer.kickPlayer(StarColors.color(punishment.formatKick())); //TODO this doesn't provide an id right away though
                }
            }

            if (punishment.getType() == PunishmentType.BLACKLIST) {
                if (punishment.isActive()) {
                    Set<IPEntry> playerIps = new HashSet<>();
                    for (IPEntry ipEntry : NexusReborn.getPlayerManager().getIpHistory()) {
                        if (ipEntry.getUuid().equals(targetPlayer.getUniqueId())) {
                            playerIps.add(ipEntry);
                        }
                    }

                    Set<UUID> alts = new HashSet<>();
                    for (IPEntry playerIp : playerIps) {
                        alts.addAll(NexusReborn.getPlayerManager().getPlayersByIp(playerIp.getIp()));
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
                NexusReborn.getPrimaryDatabase().save(punishment);
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

            NexusReborn.getPunishmentManager().addPunishment(punishment);
            plugin.getPunishmentChannel().sendPunishment(punishment);
        });
        return true;
    }
    
    protected Player getPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        
        return null;
    }
}
