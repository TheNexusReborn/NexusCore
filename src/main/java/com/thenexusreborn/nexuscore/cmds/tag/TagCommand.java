package com.thenexusreborn.nexuscore.cmds.tag;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;

public class TagCommand extends NexusCommand<NexusCore> {

    public TagCommand(NexusCore plugin) {
        super(plugin, "tag", "", Rank.MEMBER);
        this.playerOnly = true;
        
        this.subCommands.add(new TagListSubcommand(plugin, this));
        this.subCommands.add(new TagResetSubCommand(plugin, this));
        this.subCommands.add(new TagSetSubcommand(plugin, this));
    }
}