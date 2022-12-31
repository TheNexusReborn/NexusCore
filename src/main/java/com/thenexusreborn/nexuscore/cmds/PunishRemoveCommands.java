package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusProfile;
import com.thenexusreborn.api.punishment.PardonInfo;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PunishRemoveCommands implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
    
        NexusProfile target = SpigotUtils.getProfileFromCommand(sender, args[0]);
        if (target == null) return true;
    
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
            NexusAPI.getApi().getPrimaryDatabase().pushSilent(punishment);
            NexusAPI.getApi().getNetworkManager().send("removepunishment", punishment.getId() + "");
            StaffChat.sendPunishmentRemoval(punishment);
        }
        
        return true;
    }
}
