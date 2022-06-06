package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class PunishmentHistoryCmds implements CommandExecutor {
    
    private NexusCore plugin;
    
    public PunishmentHistoryCmds(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.HELPER.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " <target> [page]"));
            return true;
        }
    
        NexusPlayer target;
        try {
            UUID targetUUID = UUID.fromString(args[0]);
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(targetUUID);
            if (target == null) {
                target = NexusAPI.getApi().getDataManager().loadPlayer(targetUUID);
            }
        } catch (Exception e) {
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(args[0]);
            if (target == null) {
                target = NexusAPI.getApi().getDataManager().loadPlayer(args[0]);
            }
        }
        
        if (target == null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid target. Have they joined before?"));
            return true;
        }
    
        String actor;
        if (sender instanceof Player) {
            actor = ((Player) sender).getUniqueId().toString();
        } else {
            actor = sender.getName();
        }
        
        Set<Punishment> unfilteredPunishments = new TreeSet<>();
        if (cmd.getName().equals("staffhistory")) {
            for (Punishment punishment : NexusAPI.getApi().getPunishmentManager().getPunishments()) {
                if (punishment.getActor().equals(actor)) {
                    unfilteredPunishments.add(punishment);
                }
            }
        } else if (cmd.getName().equals("history")) {
            for (Punishment punishment : NexusAPI.getApi().getPunishmentManager().getPunishments()) {
                if (punishment.getTarget().equalsIgnoreCase(target.getUniqueId().toString())) {
                    unfilteredPunishments.add(punishment);
                }
            }
        }
        
        if (unfilteredPunishments.size() == 0) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not find any punishments."));
            return true;
        }
        
        List<Punishment> punishments = new LinkedList<>(unfilteredPunishments);
        
        int page = 0;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException e) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid page number."));
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
        
        sender.sendMessage(MCUtils.color("&6&l>> &e" + type + " for " + target.getName() + " Page " + (page + 1) + "/" + totalPages));
        boolean isStaff = cmd.getName().equals("staffhistory");
        for (int i = start; i < end; i++) {
            Punishment punishment = punishments.get(i);
            String pType = punishment.getType().getColor() + punishment.getType().getVerb();
            String timeLeft = punishment.formatTimeLeft();
            if (timeLeft != null && !timeLeft.equals("")) {
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
            sender.sendMessage(MCUtils.color("&6&l> &f" + message));
        }
    
        return true;
    }
}
