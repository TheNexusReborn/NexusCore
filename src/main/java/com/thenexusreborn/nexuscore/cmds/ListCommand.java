package com.thenexusreborn.nexuscore.cmds;

import com.google.common.collect.TreeMultimap;
import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ListCommand extends NexusCommand<NexusCore> {
    public ListCommand(NexusCore plugin) {
        super(plugin, "list", "", Rank.MEMBER);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (sender instanceof ConsoleCommandSender) {
            senderRank = Rank.NEXUS; //Override to allow console to see nexus team, as only Nexus Team have console access
        }

        int playerCount = 0;
        TreeMultimap<Rank, NexusPlayer> playerList = TreeMultimap.create();
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer == null) {
                continue;
            }

            if (nexusPlayer.getToggleValue("vanish") || nexusPlayer.getToggleValue("incognito")) {
                if (nexusPlayer.getRank().ordinal() < senderRank.ordinal()) {
                    continue;
                }
            }

            playerList.put(nexusPlayer.getEffectiveRank(), nexusPlayer);
            playerCount++;
        }

        sender.sendMessage(MsgType.INFO.format("There are %v players online on The Nexus Reborn", playerCount));
        for (Map.Entry<Rank, Collection<NexusPlayer>> entry : playerList.asMap().entrySet()) {
            Rank rank = entry.getKey();
            Collection<NexusPlayer> players = entry.getValue();
            Iterator<NexusPlayer> playersIterator = players.iterator();
            StringBuilder sb = new StringBuilder();
            while (playersIterator.hasNext()) {
                NexusPlayer player = playersIterator.next();
                sb.append(player.getName());
                if (playersIterator.hasNext()) {
                    sb.append(", ");
                }
            }
            if (!sb.isEmpty()) {
                String rankName = rank.getColor() + (rank.isBold() ? "&l" : "") + rank.name().replace("_", " ");
                sender.sendMessage(StarColors.color("  &6&l> " + rankName + "&8: &f" + sb));
            }
        }

        return true;
    }
}
