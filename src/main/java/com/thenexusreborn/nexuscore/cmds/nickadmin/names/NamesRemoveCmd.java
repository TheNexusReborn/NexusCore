package com.thenexusreborn.nexuscore.cmds.nickadmin.names;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class NamesRemoveCmd extends SubCommand<NexusCore> {
    public NamesRemoveCmd(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 1, "remove", "Removes one or more names from the random names list", Rank.ADMIN, "r");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Set<String> randomNames = NexusAPI.getApi().getRandomNames();
        
        for (String arg : args) {
            String name = arg.toLowerCase();
            if (randomNames.contains(name)) {
                randomNames.remove(name);
                MsgType.INFO.send(sender, "You removed %v from the list of random names.", name);
            } else {
                MsgType.WARN.send(sender, "That name is not on the list of random names.");
                return true;
            }
        }
        
        return true;
    }
}
