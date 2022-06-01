package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.util.UUID;

public class PunishRemoveCommands implements CommandExecutor {
    
    private NexusCore plugin;
    
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
    
        Punishment punishment = NexusAPI.getApi().getPunishmentManager().getPunishmentByTarget(target.getUniqueId());
        
        
        return true;
    }
}
