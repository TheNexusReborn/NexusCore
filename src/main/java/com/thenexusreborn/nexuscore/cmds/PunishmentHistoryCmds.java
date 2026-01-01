package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("ExtractMethodRecommender")
public class PunishmentHistoryCmds implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public PunishmentHistoryCmds(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(sender);
        if (senderRank.ordinal() > Rank.HELPER.ordinal()) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Usage: /" + label + " <target> [page]"));
            return true;
        }
        
        String commandTarget, commandTargetName;
        try {
            UUID uuid = UUID.fromString(args[0]);
            commandTarget = uuid.toString();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                commandTargetName = player.getName();
            } else {
                commandTargetName = NexusReborn.getPlayerManager().getNameFromUUID(uuid); //Can be replaced with the BiMap idea
            }
        } catch (Exception e) {
            commandTargetName = args[0];
            if (commandTargetName.equalsIgnoreCase("PowerMoveRegulator") || commandTargetName.equalsIgnoreCase("Console")) {
                commandTarget = commandTargetName;
            } else {
                UUID uuidFromName = NexusReborn.getPlayerManager().getUUIDFromName(commandTargetName);
                if (uuidFromName != null) {
                    commandTarget = uuidFromName.toString();
                } else {
                    commandTarget = null;
                }
            }
        }
        
        Set<Punishment> unfilteredPunishments = new TreeSet<>();
        if (cmd.getName().equals("staffhistory")) {
            for (Punishment punishment : NexusReborn.getPunishmentManager().getPunishments()) {
                if (punishment.getActor().equals(commandTarget)) {
                    unfilteredPunishments.add(punishment);
                }
            }
        } else if (cmd.getName().equals("history")) {
            for (Punishment punishment : NexusReborn.getPunishmentManager().getPunishments()) {
                if (punishment.getTarget().equalsIgnoreCase(commandTarget)) {
                    unfilteredPunishments.add(punishment);
                }
            }
        }
        
        if (unfilteredPunishments.isEmpty()) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Could not find any punishments."));
            return true;
        }
        
        List<Punishment> punishments = new LinkedList<>(unfilteredPunishments);
        
        int page = 0;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException e) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "Invalid page number."));
                return true;
            }
        }
        
        int totalPages = Math.floorDiv(punishments.size(), 7);
        
        int start = page * 7;
        int end = Math.min(start + 7, punishments.size());
        
        String type = "";
        if (cmd.getName().equalsIgnoreCase("staffhistory")) {
            type = "Staff History";
        } else if (cmd.getName().equalsIgnoreCase("history")) {
            type = "History";
        }
        
        sender.sendMessage(StarColors.color("&6&l>> &e" + type + " for " + commandTargetName + " Page " + (page + 1) + "/" + totalPages));
        boolean isStaff = cmd.getName().equals("staffhistory");
        for (int i = start; i < end; i++) {
            Punishment punishment = punishments.get(i);
            String pType = punishment.getType().getColor() + punishment.getType().getVerb();
            String timeLeft = punishment.formatTimeLeft();
            if (timeLeft != null && !timeLeft.isEmpty()) {
                timeLeft = " &c(" + timeLeft + ")";
            }
            String actorName = "&b" + punishment.getActorNameCache();
            String targetName = "&d" + punishment.getTargetNameCache();
            String reason = "&3" + punishment.getReason();
            String pardoned = "";
            if (punishment.getPardonInfo() != null) {
                pardoned = " &9(Pardoned)";
            }
            String message;
            if (isStaff) {
                message = actorName + " " + pType + " " + targetName + " &ffor " + reason + timeLeft + pardoned;
            } else {
                message = targetName + " &fwas " + pType + " &fby " + actorName + " &ffor " + reason + timeLeft + pardoned;
            }
            sender.sendMessage(StarColors.color("&6&l> &f" + message));
        }
        
        return true;
    }
}
