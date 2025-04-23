package com.thenexusreborn.nexuscore.cmds.rank;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;

@SuppressWarnings("DuplicatedCode")
public class RankCommand extends NexusCommand<NexusCore> {

    public RankCommand(NexusCore plugin) {
        super(plugin, "rank", "Manage ranks", Rank.ADMIN);
        
        //New command usage
        // /rank <add|set|remove> <player> <rank> 
        
        this.subCommands.add(new RankSetSubCommand(plugin, this));
        this.subCommands.add(new RankAddSubCommand(plugin, this));
        this.subCommands.add(new RankRemoveSubCommand(plugin, this));
        this.subCommands.add(new RankListSubCommand(plugin, this));
    }
}
