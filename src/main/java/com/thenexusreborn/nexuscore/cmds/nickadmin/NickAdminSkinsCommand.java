package com.thenexusreborn.nexuscore.cmds.nickadmin;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.cmds.nickadmin.skins.SkinsAddCmd;
import com.thenexusreborn.nexuscore.cmds.nickadmin.skins.SkinsRemoveCmd;

public class NickAdminSkinsCommand extends SubCommand<NexusCore> {
    public NickAdminSkinsCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "skins", "Manages the nickname random skins list", Rank.ADMIN, "s");
        
        this.subCommands.add(new SkinsAddCmd(plugin, this));
        this.subCommands.add(new SkinsRemoveCmd(plugin, this));
    }
}
