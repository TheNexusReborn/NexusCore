package com.thenexusreborn.nexuscore.cmds.tag.admin;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import org.bukkit.command.CommandSender;

public class TagAdminRemoveSubCommand extends TagAdminSubCommand {
    public TagAdminRemoveSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "remove", "", "r");
    }

    @Override
    protected void handle(CommandSender sender, NexusPlayer nexusPlayer, Tag tag, String tagName) {
        nexusPlayer.removeTag(tagName);
        NexusReborn.getPrimaryDatabase().deleteSilent(Tag.class, tag.getId());
        sender.sendMessage(StarColors.color("&eYou remove the tag " + tag.getDisplayName() + " &efrom &b" + nexusPlayer.getName() + "&e's unlockedTags"));
    }
}
