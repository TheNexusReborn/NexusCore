package com.thenexusreborn.nexuscore.cmds.servers;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;

public class ServersCommand extends NexusCommand<NexusCore> {

    public ServersCommand(NexusCore plugin) {
        super(plugin, "servers", "", Rank.ADMIN);
        this.subCommands.add(new ServersInfoSubCommand(plugin, this));
        this.subCommands.add(new ServersPlayersSubCommand(plugin, this));
        this.subCommands.add(new ServersStateSubCommand(plugin, this));
        this.subCommands.add(new ServersListSubCommand(plugin, this));
    }
}