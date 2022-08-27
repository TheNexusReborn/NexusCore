package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class PunishRemoveCommands implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public PunishRemoveCommands(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // /un<type> <name> <reason>
    
        PunishmentType type = null;
        boolean all = false;
        if (cmd.getName().equalsIgnoreCase("unban")) {
            type = PunishmentType.BAN;
        } else if (cmd.getName().equalsIgnoreCase("unmute")) {
            type = PunishmentType.MUTE;
        } else if (cmd.getName().equalsIgnoreCase("unblacklist")) {
            type = PunishmentType.BLACKLIST;
        } else if (cmd.getName().equalsIgnoreCase("pardon")) {
            all = true;
        }
    
        if (type == null && !all) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid punishment type."));
            return true;
        }
    
        if (!(args.length > 1)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " <target> <reason>"));
            return true;
        }
    
        NexusPlayer target;
        try {
            UUID uuid = UUID.fromString(args[0]);
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
            if (target == null) {
                target = NexusAPI.getApi().getDataManager().loadPlayer(uuid);
            }
        } catch (Exception e) {
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(args[0]);
            if (target == null) {
                target = NexusAPI.getApi().getDataManager().loadPlayer(args[0]);
            }
        }
        
        if (target == null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid target provided."));
            return true;
        }
    
        List<Punishment> punishments = NexusAPI.getApi().getPunishmentManager().getPunishmentsByTarget(target.getUniqueId());
        if (punishments.size() == 0) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "That player does not have any punishments"));
            return true;
        }
        
        List<Punishment> activePunishments = new ArrayList<>();
        for (Punishment punishment : punishments) {
            if (punishment.getType() == type || all) {
                if (punishment.isActive()) {
                    activePunishments.add(punishment);
                }
            }
        }
        
        if (activePunishments.size() == 0) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "That player does not have any active punishmentss."));
            return true;
        }
        
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        
        String reason = reasonBuilder.toString().trim();
    
        String actor;
        if (sender instanceof Player) {
            actor = ((Player) sender).getUniqueId().toString();
        } else {
            actor = sender.getName();
        }
        
        PardonInfo info = new PardonInfo(System.currentTimeMillis(), actor, reason);
        for (Punishment punishment : activePunishments) {
            punishment.setPardonInfo(info);
            NexusAPI.getApi().getPrimaryDatabase().push(punishment);
            NexusAPI.getApi().getNetworkManager().send("removepunishment", punishment.getId() + "");
            StaffChat.sendPunishmentRemoval(punishment);
        }
        
        return true;
    }
}
