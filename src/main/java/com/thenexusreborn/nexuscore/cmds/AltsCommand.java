package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.helper.StringHelper;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.util.*;

public class AltsCommand implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public AltsCommand(NexusCore plugin) {
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
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " <target>"));
            return true;
        }
    
        CachedPlayer target = null;
        try {
            UUID targetUUID = UUID.fromString(args[0]);
            target = NexusAPI.getApi().getPlayerManager().getCachedPlayers().get(targetUUID);
        } catch (Exception e) {
            for (CachedPlayer cachedPlayer : NexusAPI.getApi().getPlayerManager().getCachedPlayers().values()) {
                if (cachedPlayer.getName().equalsIgnoreCase(args[0])) {
                    target = cachedPlayer;
                }
            }
        }
    
        if (target == null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid target. Have they joined before?"));
            return true;
        }
    
        Set<String> ips = new HashSet<>();
        for (IPEntry ipEntry : target.getIpHistory()) {
            ips.add(ipEntry.getIp());
        }
        
        Set<UUID> players = new HashSet<>();
        for (String ip : ips) {
            players.addAll(NexusAPI.getApi().getPlayerManager().getPlayersByIp(ip));
        }
        
        Set<String> altNames = new HashSet<>();
        for (UUID player : players) {
            altNames.add(NexusAPI.getApi().getPlayerManager().getCachedPlayers().get(player).getName());
        }
        
        String altNameList = StringHelper.join(altNames, ", ");
        sender.sendMessage(MCUtils.color(MsgType.INFO + target.getName() + " has the following alt accounts..."));
        sender.sendMessage(MCUtils.color("&6&l> &b" + altNameList));
        return true;
    }
}
