package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.cmdflags.FlagResult;
import com.stardevllc.colors.StarColors;
import com.stardevllc.helper.Pair;
import com.stardevllc.helper.StringHelper;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IPEntry;
import com.thenexusreborn.api.player.PlayerManager;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AltsCommand extends NexusCommand<NexusCore> {
    
    public AltsCommand(NexusCore plugin) {
        super(plugin, "alts", "", Rank.MOD);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
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
            String nameFromUUID = playerManager.getNameFromUUID(player);
            if (nameFromUUID != null && !nameFromUUID.isEmpty()) {
                Rank altRank = playerManager.getPlayerRank(player);
                if (altRank.ordinal() >= playerRank.ordinal()) {
                    altNames.add(nameFromUUID);
                }
            } else {
                plugin.getLogger().warning("Found alt " + player.toString() + " for " + playerInfo.value() + " but could not get the name. Cache returned: " + nameFromUUID);
            }
        }

        String altNameList = StringHelper.join(altNames, ", ");
        sender.sendMessage(StarColors.color(MsgType.INFO + playerInfo.value() + " has the following alt accounts..."));
        sender.sendMessage(StarColors.color("&6&l> &b" + altNameList));
        return true;
    }
}
