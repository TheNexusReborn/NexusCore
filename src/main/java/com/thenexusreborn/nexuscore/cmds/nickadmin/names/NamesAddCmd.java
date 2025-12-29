package com.thenexusreborn.nexuscore.cmds.nickadmin.names;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class NamesAddCmd extends SubCommand<NexusCore> {
    public NamesAddCmd(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 1, "add", "Adds one or more players to the random names list", Rank.ADMIN, "a");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Set<String> randomNames = NexusReborn.getRandomNames();
        
        for (String arg : args) {
            String name = arg.toLowerCase();
            if (!randomNames.contains(name)) {
                randomNames.add(name);
                MsgType.INFO.send(sender, "You added %v to the list of random names.", name);
            } else {
                MsgType.WARN.send(sender, "That name is already on the list of random names.");
                return true;
            }
        }
        
        return true;
    }
}
