package com.thenexusreborn.nexuscore.cmds.tag.admin;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public abstract class TagAdminSubCommand extends SubCommand<NexusCore> {
    public TagAdminSubCommand(NexusCore plugin, ICommand<NexusCore> parent, String name, String description, String... aliases) {
        super(plugin, parent, 0, name, description, Rank.ADMIN, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 1)) {
            sender.sendMessage(StarColors.color("&cUsage: /tagadmin " + label + " <player> <tagName>"));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String tagName = sb.substring(0, sb.length() - 1);

        UUID uniqueId = NexusReborn.getPlayerManager().getUUIDFromName(args[0]);
        if (uniqueId == null) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "That player has not yet joined the server."));
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Tag> tags;
            try {
                tags = NexusReborn.getPrimaryDatabase().get(Tag.class, "uuid", uniqueId.toString());
            } catch (SQLException e) {
                sender.sendMessage(StarColors.color(MsgType.ERROR + "There was a database error while getting the list of tags."));
                return;
            }

            Tag tag = null;
            for (Tag t : tags) {
                if (t.getName().equalsIgnoreCase(tagName)) {
                    tag = t;
                    break;
                }
            }

            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(uniqueId);

            handle(sender, nexusPlayer, tag, tagName);
        });
        return true;
    }

    protected abstract void handle(CommandSender sender, NexusPlayer nexusPlayer, Tag tag, String tagName);
}
