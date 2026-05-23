package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.minecraft.StarColors;
import com.stardevllc.minecraft.command.flags.FlagResult;
import com.stardevllc.starlib.tuple.pair.Pair;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.*;

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
        
        PunishmentManager punishmentManager = NexusReborn.getPunishmentManager();
        PlayerManager playerManager = NexusReborn.getPlayerManager();
        Pair<UUID, String> playerInfo = playerManager.getPlayerFromIdentifier(args[0]);
        if (playerInfo == null) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Could not find a player with that information."));
            return true;
        }
        
        Rank playerRank = playerManager.getPlayerRank(playerInfo.getLeft());
        if (playerRank.ordinal() < senderRank.ordinal()) {
            sender.sendMessage(MsgType.WARN.format("You do not have permission to view %v's alt ccounts", playerInfo.getLeft()));
            return true;
        }
        
        MsgType.VERBOSE.send(sender, "Please wait while we process your request...");
        AltFinder altFinder = new AltFinder(playerInfo.getLeft());
        altFinder.search();
        
        Set<String> names = new HashSet<>();
        for (UUID uuid : altFinder.getUuids()) {
            System.out.println(uuid);
            String nameFromUUID = playerManager.getNameFromUUID(uuid);
            System.out.println("Name from UUID: " + nameFromUUID);
            if (nameFromUUID != null && !nameFromUUID.isEmpty()) {
                Rank altRank = playerManager.getPlayerRank(uuid);
                if (altRank.ordinal() < senderRank.ordinal()) {
                    System.out.println(altRank.name() + "(" + altRank.ordinal() + ")" + " < " + senderRank.name() + "(" + senderRank.ordinal() + ")");
                    continue;
                }
                
                List<Punishment> punishments = punishmentManager.getPunishmentsByTarget(uuid);
                System.out.println("Punishments: " + punishments.size());
                
                punishments.removeIf(p -> !p.isActive());
                punishments.removeIf(p -> p.getType() == PunishmentType.KICK);
                punishments.removeIf(p -> p.getType() == PunishmentType.WARN);
                
                if (!punishments.isEmpty()) {
                    for (Punishment punishment : punishments) {
                        if (!punishment.isActive()) {
                            System.out.println("Punishment is not active");
                            continue;
                        }
                        
                        System.out.println("Type: " + punishment.getType().name());
                        
                        if (punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.BLACKLIST) {
                            names.add("&c" + nameFromUUID);
                            break;
                        } else if (punishment.getType() == PunishmentType.MUTE) {
                            names.add("&9" + nameFromUUID);
                            break;
                        } else {
                            names.add("&7" + nameFromUUID);
                            break;
                        }
                    }
                } else {
                    names.add("&7" + nameFromUUID);
                }
            } else {
                plugin.getLogger().warning("Found alt " + uuid.toString() + " for " + playerInfo.getRight() + " but could not get the name. Cache returned: " + nameFromUUID);
            }
        }

        String altNameList = String.join("&e, ", names);
        sender.sendMessage(StarColors.color(MsgType.INFO + playerInfo.getRight() + " has the following alt accounts..."));
        sender.sendMessage(StarColors.color(MsgType.INFO + "  &cBanned  &9Muted  &7Good Standing"));
        sender.sendMessage(StarColors.color("&6&l> &b" + altNameList));
        return true;
    }
}
