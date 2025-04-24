package com.thenexusreborn.nexuscore.cmds.nickadmin;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.cmds.nickadmin.blacklist.BlacklistAddCmd;
import com.thenexusreborn.nexuscore.cmds.nickadmin.blacklist.BlacklistRemoveCmd;

public class NickAdminBlacklistCommand extends SubCommand<NexusCore> {
    public NickAdminBlacklistCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "blacklist", "Manages the nickname blacklist", Rank.ADMIN, "bl");
        
        this.subCommands.add(new BlacklistAddCmd(plugin, this));
        this.subCommands.add(new BlacklistRemoveCmd(plugin, this));
    }
}
