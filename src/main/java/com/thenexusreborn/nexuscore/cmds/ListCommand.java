package com.thenexusreborn.nexuscore.cmds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.stardevllc.colors.StarColors;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ListCommand implements CommandExecutor {
    
    private NexusCore plugin;

    public ListCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (sender instanceof ConsoleCommandSender) {
            senderRank = Rank.NEXUS; //Override to allow console to see nexus team, as only Nexus Team have console access
        }
        Multimap<Rank, NexusPlayer> playerList = HashMultimap.create();
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer == null) {
                continue; //Should never happen
            }

            if (nexusPlayer.getToggleValue("vanish") || nexusPlayer.getToggleValue("incognito")) {
                if (nexusPlayer.getRank().ordinal() < senderRank.ordinal()) {
                    continue;
                }
            }

            playerList.put(nexusPlayer.getRank(), nexusPlayer);
        }

        sender.sendMessage(StarColors.color(MsgType.INFO + "Players online on The Nexus Reborn."));
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
                sender.sendMessage(StarColors.color("  &6&l> " + rank.getPrefix() + "&8: &f" + sb));
            }
        }
        
        return true;
    }
}
