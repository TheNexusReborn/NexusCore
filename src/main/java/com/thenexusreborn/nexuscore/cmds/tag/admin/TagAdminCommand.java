package com.thenexusreborn.nexuscore.cmds.tag.admin;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;

public class TagAdminCommand extends NexusCommand<NexusCore> {
    public TagAdminCommand(NexusCore plugin) {
        super(plugin, "tagadmin", "", Rank.ADMIN);
        
        this.subCommands.add(new TagAdminAddSubCommand(plugin, this));
        this.subCommands.add(new TagAdminRemoveSubCommand(plugin, this));
    }
}
