package com.thenexusreborn.nexuscore.cmds.nickadmin;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.cmds.nickadmin.names.NamesAddCmd;
import com.thenexusreborn.nexuscore.cmds.nickadmin.names.NamesRemoveCmd;

public class NickAdminNamesCommand extends SubCommand<NexusCore> {
    public NickAdminNamesCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "names", "Manages the nickname random names list", Rank.ADMIN, "n");
        
        this.subCommands.add(new NamesAddCmd(plugin, this));
        this.subCommands.add(new NamesRemoveCmd(plugin, this));
    }
}
