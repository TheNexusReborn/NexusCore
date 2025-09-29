package com.thenexusreborn.nexuscore.cmds.rank;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starlib.helper.Pair;
import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.stardevllc.starmclib.mojang.MojangAPI;
import com.stardevllc.starmclib.mojang.MojangProfile;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class RankListSubCommand extends SubCommand<NexusCore> {
    public RankListSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "list", "List ranks of a player", Rank.ADMIN, "l");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            sender.sendMessage(MsgType.WARN.format("Usage: /rank " + label + " <player>"));
            return true;
        }
        
        MsgType.VERBOSE.send(sender, "Processing request...");
        
        PlayerManager playerManager = NexusReborn.getPlayerManager();
        Pair<UUID, String> playerInfo = playerManager.getPlayerFromIdentifier(args[0]);
        if (playerInfo == null) {
            try {
                UUID uuid = UUID.fromString(args[0]);
                MojangProfile mojangProfile = MojangAPI.getProfile(uuid);
                if (mojangProfile != null) {
                    playerInfo = new Pair<>(uuid, mojangProfile.getName());
                }
            } catch (Exception e) {
                MojangProfile mojangProfile = MojangAPI.getProfile(args[0]);
                if (mojangProfile != null) {
                    playerInfo = new Pair<>(mojangProfile.getUniqueId(), mojangProfile.getName());
                }
            }
        }
        
        if (playerInfo == null) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Could not find a player with that identifier."));
            return true;
        }
        
        PlayerRanks playerRanks = playerManager.getPlayerRanks(playerInfo.first());
        if (playerRanks == null) {
            MsgType.WARN.send(sender, "That player has no ranks");
            return true;
        }
        
        Map<Rank, Long> ranks = playerRanks.findAll();
        StarColors.coloredMessage(sender, "&6&l>> &eList of ranks for &b" + playerInfo.second());
        ranks.forEach((rank, expire) -> StarColors.coloredMessage(sender, " &6&l> " + rank.getColor() + rank.name().replace("_", " ")));
        
        return true;
    }
}
