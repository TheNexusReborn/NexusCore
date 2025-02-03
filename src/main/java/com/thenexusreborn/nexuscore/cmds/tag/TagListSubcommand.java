package com.thenexusreborn.nexuscore.cmds.tag;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;

import java.util.Set;

public class TagListSubcommand extends TagSubCommand {
    public TagListSubcommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "list", "", "l");
    }

    @Override
    protected void handle(NexusPlayer nexusPlayer, String[] args) {
        Set<String> unlockedTags = nexusPlayer.getTags();
        if (!unlockedTags.isEmpty()) {
            nexusPlayer.sendMessage("&eList of available tags...");
            for (String rawTag : unlockedTags) {
                Tag tag = new Tag(null, rawTag, 0);
                nexusPlayer.sendMessage(" &8- &e" + tag.getName() + " " + tag.getDisplayName());
            }
        } else {
            nexusPlayer.sendMessage("&cYou have no tags unlocked.");
        }
    }
}
