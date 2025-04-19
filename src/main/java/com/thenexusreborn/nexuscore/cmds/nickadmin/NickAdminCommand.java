package com.thenexusreborn.nexuscore.cmds.nickadmin;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;

public class NickAdminCommand extends NexusCommand<NexusCore> {
    public NickAdminCommand(NexusCore plugin) {
        super(plugin, "nickadmin", "Allows management of nicknames", Rank.ADMIN, "na");
        
        this.subCommands.add(new NickAdminBlacklistCommand(plugin, this));
        this.subCommands.add(new NickAdminNamesCommand(plugin, this));
        this.subCommands.add(new NickAdminSkinsCommand(plugin, this));
    }
}
