package com.thenexusreborn.nexuscore.cmds.rank;

import com.stardevllc.helper.Pair;
import com.stardevllc.mojang.MojangAPI;
import com.stardevllc.mojang.MojangProfile;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.stardevllc.time.TimeParser;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.api.sql.objects.codecs.RanksCodec;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public abstract class RankModifySubcommand extends SubCommand<NexusCore> {
    public RankModifySubcommand(NexusCore plugin, ICommand<NexusCore> parent, String name, String description, String... aliases) {
        super(plugin, parent, 0, name, description, Rank.ADMIN, "s", "add", "a");
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 1)) {
            sender.sendMessage(MsgType.WARN.format("Usage: /rank " + label + " <player> <rank> [length]"));
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

        UUID targetUniqueID = playerInfo.key();
        String targetName = playerInfo.value();
        PlayerRanks targetRanks = playerManager.getPlayerRanks(targetUniqueID);
        
        if (targetRanks == null) {
            targetRanks = new PlayerRanks(targetUniqueID);
        }
        
        boolean isFirestar311 = sender instanceof Player player && player.getUniqueId().toString().equals("3f7891ce-5a73-4d52-a2ba-299839053fdc");

        if (senderRank.ordinal() >= targetRanks.get().ordinal()) {
            if (!(sender instanceof ConsoleCommandSender)) {
                if (!isFirestar311) {
                    sender.sendMessage(StarColors.color("&cYou cannot modify " + targetName + "'s rank as they have " + targetRanks.get().name() + " and you have " + senderRank.name()));
                    return true;
                }
            }
        }

        Rank rank;
        try {
            rank = Rank.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(StarColors.color("&cYou provided an invalid rank name."));
            return true;
        }

        if (rank == Rank.NEXUS) {
            if (sender instanceof Player player) {
                if (!player.getUniqueId().toString().equalsIgnoreCase("3f7891ce-5a73-4d52-a2ba-299839053fdc")) {
                    sender.sendMessage(StarColors.color("&cYou cannot modify " + args[0] + "'s rank to " + Rank.NEXUS.name() + " as it is equal to or higher than your own."));
                    return true;
                }
            }
        } else if (senderRank.ordinal() >= rank.ordinal() && (sender instanceof Player player)) {
            if (!player.getUniqueId().toString().equalsIgnoreCase("3f7891ce-5a73-4d52-a2ba-299839053fdc")) {
                sender.sendMessage(StarColors.color("&cYou cannot modify " + args[0] + "'s rank to " + rank.name() + " as it is equal to or higher than your own."));
                return true;
            }
        }

        String rankName = rank.getColor() + "&l" + rank.name();

        long time = -1;
        if (args.length > 2) {
            time = new TimeParser().parseTime(args[2]);
        }

        long expire = -1;
        if (targetRanks.contains(rank)) {
            long existingTime = System.currentTimeMillis() - targetRanks.getExpire(rank);
            if (existingTime > 0) {
                expire = System.currentTimeMillis() + time + existingTime;
            }
        }

        if (time > 0 && expire == -1) {
            expire = System.currentTimeMillis() + time;
        }
        
        handle(sender, rank, expire, targetRanks, targetName, rankName, time);
        
        if (Bukkit.getPlayer(targetUniqueID) != null) {
            playerManager.updatePermissions(targetUniqueID, targetRanks);
        }
        
        SQLDatabase database = NexusReborn.getPrimaryDatabase();
        
        try {
            String encodedRanks = new RanksCodec().encode(targetRanks);
            database.execute(String.format("insert into `players`(`ranks`, `name`, `uniqueid`) values ('%s','%s','%s') on duplicate key update `ranks`='%s';", encodedRanks, playerInfo.value(), playerInfo.key().toString(), encodedRanks));
        } catch (SQLException e) {
            MsgType.ERROR.send(sender, "There was an error while saving changes to the database.");
            e.printStackTrace();
        }
        
        return true;
    }
    
    protected abstract void handle(CommandSender sender, Rank rank, long expire, PlayerRanks targetRanks, String targetName, String rankName, long time);
}
