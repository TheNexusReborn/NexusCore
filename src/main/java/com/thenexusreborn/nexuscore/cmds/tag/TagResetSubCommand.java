package com.thenexusreborn.nexuscore.cmds.tag;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;

public class TagResetSubCommand extends TagSubCommand {
    public TagResetSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "reset", "", "r", "rs", "remove", "rm");
    }

    @Override
    protected void handle(NexusPlayer nexusPlayer, String[] args) {
        nexusPlayer.setActiveTag(null);
        nexusPlayer.sendMessage("&eYou reset your tag.");
    }
}
