package com.thenexusreborn.nexuscore.cmds.tag;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;

public class TagSetSubcommand extends TagSubCommand {
    public TagSetSubcommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "set", "", "s");
    }

    @Override
    protected void handle(NexusPlayer nexusPlayer, String[] args) {
        if (!(args.length > 0)) {
            nexusPlayer.sendMessage("&cYou must provide a tag name.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String tagName = sb.substring(0, sb.length() - 1);

        if (!nexusPlayer.isTagUnlocked(tagName)) {
            nexusPlayer.sendMessage("&cYou do not have a tag with that name.");
            return;
        }

        nexusPlayer.setActiveTag(tagName);
        nexusPlayer.sendMessage("&eYou set your tag to " + nexusPlayer.getActiveTag().getDisplayName());
    }
}
