package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.stardevllc.starlib.Pair;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.PlayerManager;
import com.thenexusreborn.api.punishment.PardonInfo;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class PunishRemoveCommands implements CommandExecutor {
    
    private NexusCore plugin;

    public PunishRemoveCommands(NexusCore plugin) {
        this.plugin = plugin;
    }

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
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "Invalid punishment type."));
            return true;
        }
    
        if (!(args.length > 1)) {
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "Usage: /" + label + " <target> <reason>"));
            return true;
        }

        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
        Pair<UUID, String> playerInfo = playerManager.getPlayerFromIdentifier(args[0]);
        if (playerInfo == null) {
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "Could not find a player with that identifier."));
            return true;
        }

        UUID targetUniqueID = playerInfo.key();
    
        List<Punishment> punishments = NexusAPI.getApi().getPunishmentManager().getPunishmentsByTarget(targetUniqueID);
        if (punishments.isEmpty()) {
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "That player does not have any punishments"));
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
        
        if (activePunishments.isEmpty()) {
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "That player does not have any active punishmentss."));
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
            NexusAPI.getApi().getPrimaryDatabase().saveSilent(punishment);
            plugin.getPunishmentChannel().sendPunishmentRemoval(punishment);
        }
        
        return true;
    }
}
