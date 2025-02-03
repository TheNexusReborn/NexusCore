package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.colors.StarColors;
import com.stardevllc.helper.Pair;
import com.stardevllc.helper.StringHelper;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IPEntry;
import com.thenexusreborn.api.player.PlayerManager;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AltsCommand implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public AltsCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.HELPER.ordinal()) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
    
        if (!(args.length > 0)) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Usage: /" + label + " <target>"));
            return true;
        }

        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
        Pair<UUID, String> playerInfo = playerManager.getPlayerFromIdentifier(args[0]);
        if (playerInfo == null) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Could not find a player with that information."));
            return true;
        }
        
        Rank playerRank = playerManager.getPlayerRank(playerInfo.key());
        if (playerRank.ordinal() < senderRank.ordinal()) {
            sender.sendMessage(MsgType.WARN.format("You do not have permission to view %v's alt ccounts", playerInfo.key()));
            return true;
        }

        Set<String> ips = new HashSet<>();
        for (IPEntry ipEntry : playerManager.getIpHistory()) {
            if (ipEntry.getUuid().equals(playerInfo.key())) {
                ips.add(ipEntry.getIp());
            }
        }
        
        Set<UUID> players = new HashSet<>();
        for (String ip : ips) {
            players.addAll(playerManager.getPlayersByIp(ip));
        }
        
        Set<String> altNames = new HashSet<>();
        for (UUID player : players) {
            altNames.add(playerManager.getNameFromUUID(player));
        }
        
        String altNameList = StringHelper.join(altNames, ", ");
        sender.sendMessage(StarColors.color(MsgType.INFO + playerInfo.value() + " has the following alt accounts..."));
        sender.sendMessage(StarColors.color("&6&l> &b" + altNameList));
        return true;
    }
}
