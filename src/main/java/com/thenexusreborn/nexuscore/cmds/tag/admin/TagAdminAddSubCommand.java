package com.thenexusreborn.nexuscore.cmds.tag.admin;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class TagAdminAddSubCommand extends TagAdminSubCommand{

    public TagAdminAddSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "add", "", "a");
    }

    @Override
    protected void handle(CommandSender sender, NexusPlayer nexusPlayer, Tag tag, String tagName) {
        if (tag != null) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "That player already has that tag unlocked."));
            return;
        }

        tag = new Tag(nexusPlayer.getUniqueId(), tagName, System.currentTimeMillis());

        if (nexusPlayer != null) {
            nexusPlayer.addTag(tag);
        }

        NexusReborn.getPrimaryDatabase().saveSilent(tag);

        String playerName = nexusPlayer.getName();
        sender.sendMessage(StarColors.color("&eYou added the tag " + tag.getDisplayName() + " &eto &b" + playerName + "&e's unlocked tags"));
    }
}
