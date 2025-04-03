package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SayCommand extends NexusCommand<NexusCore> {
    
    public SayCommand(NexusCore plugin) {
        super(plugin, "say", "", Rank.HELPER);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        //TODO Make this a channel that is per server, Broadcast will be global
        Bukkit.broadcastMessage(StarColors.color("&8[&f&l&oSAY&8] " + senderRank.getColor() + sender.getName() + "&8: &b" + sb));
        return true;
    }
}
