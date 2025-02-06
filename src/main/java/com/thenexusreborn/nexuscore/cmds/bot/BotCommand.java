package com.thenexusreborn.nexuscore.cmds.bot;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;

public class BotCommand extends NexusCommand<NexusCore> {
    
    public BotCommand(NexusCore plugin) {
        super(plugin, "nexusbot", "", Rank.ADMIN);
        this.subCommands.add(new BotStartSubCommand(plugin, this));
        this.subCommands.add(new BotStopSubCommand(plugin, this));
    }
}
