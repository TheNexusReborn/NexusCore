package com.thenexusreborn.nexuscore.cmds.rank;

import com.stardevllc.helper.Pair;
import com.stardevllc.mojang.MojangAPI;
import com.stardevllc.mojang.MojangProfile;
import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusAPI;
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
        
        PlayerManager playerManager = NexusAPI.getApi().getPlayerManager();
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
        
        PlayerRanks playerRanks = playerManager.getPlayerRanks(playerInfo.key());
        if (playerRanks == null) {
            MsgType.WARN.send(sender, "That player has no ranks");
            return true;
        }
        
        Map<Rank, Long> ranks = playerRanks.findAll();
        StarColors.coloredMessage(sender, "&6&l>> &eList of ranks for &b" + playerInfo.value());
        ranks.forEach((rank, expire) -> StarColors.coloredMessage(sender, " &6&l> " + rank.getColor() + rank.name().replace("_", " ")));
        
        return true;
    }
}
