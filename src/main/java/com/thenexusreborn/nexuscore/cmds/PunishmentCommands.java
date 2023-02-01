package com.thenexusreborn.nexuscore.cmds;

import com.starmediadev.starlib.TimeParser;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.util.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PunishmentCommands implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public PunishmentCommands(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        PunishmentType type = null;
        if (cmd.getName().equalsIgnoreCase("ban") || cmd.getName().equalsIgnoreCase("tempban") || cmd.getName().equalsIgnoreCase("tb")) {
            type = PunishmentType.BAN;
        } else if (cmd.getName().equalsIgnoreCase("mute") || cmd.getName().equalsIgnoreCase("tempmute") || cmd.getName().equalsIgnoreCase("tm")) {
            type = PunishmentType.MUTE;
        } else if (cmd.getName().equalsIgnoreCase("kick")) {
            type = PunishmentType.KICK;
        } else if (cmd.getName().equalsIgnoreCase("warn")) {
            type = PunishmentType.WARN;
        } else if (cmd.getName().equalsIgnoreCase("blacklist")) {
            type = PunishmentType.BLACKLIST;
        }
        
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
    
        NexusProfile target = SpigotUtils.getProfileFromCommand(sender, args[0]);
        if (target == null) return true;
        
        if (target.getRank().ordinal() < actorRank.ordinal()) {
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
        
        for (int i = startIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        
        String reason = sb.toString().trim();
        Punishment punishment = new Punishment(System.currentTimeMillis(), length, actor, target.getUniqueId().toString(), server, reason, type, Visibility.SILENT);
        
        if (punishment.getType() == PunishmentType.WARN) {
            punishment.setAcknowledgeInfo(new AcknowledgeInfo(Utils.generateCode(8, true, true, true)));
        }
        
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(punishment);
        if (punishment.getId() < 1) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not create the punishment. Please report to Firestar311."));
            return true;
        }
        
        NexusAPI.getApi().getNetworkManager().send("punishment", punishment.getId() + "");
        StaffChat.sendPunishment(punishment);
        return true;
    }
}
