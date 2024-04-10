package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TagCommand implements CommandExecutor {

    private final NexusCore plugin;

    public TagCommand(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            sender.sendMessage(ColorUtils.color("&cUsage: /tag <list|set|reset> [tagname]"));
            return true;
        }

        if (args[0].equalsIgnoreCase("unlock") || args[0].equalsIgnoreCase("remove")) {
            Rank senderRank = MCUtils.getSenderRank(plugin, sender);
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(ColorUtils.color("&cYou do not have permission to use that command."));
                return true;
            }

            if (!(args.length > 2)) {
                sender.sendMessage(ColorUtils.color("&cUsage: /tag " + args[0] + " <player> <tagName>"));
                return true;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String tagName = sb.substring(0, sb.length() - 1);

            UUID uniqueId = NexusAPI.getApi().getPlayerManager().getUUIDFromName(args[1]);
            if (uniqueId == null) {
                sender.sendMessage(ColorUtils.color(MsgType.WARN + "That player has not yet joined the server."));
                return true;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                List<Tag> tags;
                try {
                    tags = NexusAPI.getApi().getPrimaryDatabase().get(Tag.class, "uuid", uniqueId.toString());
                } catch (SQLException e) {
                    sender.sendMessage(ColorUtils.color(MsgType.ERROR + "There was a database error while getting the list of tags."));
                    return;
                }

                Tag tag = null;
                for (Tag t : tags) {
                    if (t.getName().equalsIgnoreCase(tagName)) {
                        tag = t;
                        break;
                    }
                }

                NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uniqueId);

                String cmdAction, verb;
                if (args[0].equalsIgnoreCase("unlock")) {
                    if (tag != null) {
                        sender.sendMessage(ColorUtils.color(MsgType.WARN + "That player already has that tag unlocked."));
                        return;
                    }

                    tag = new Tag(uniqueId, tagName, System.currentTimeMillis());
                    cmdAction = "unlocked";
                    verb = "for";
                    
                    if (nexusPlayer != null) {
                        nexusPlayer.addTag(tag);
                    }
                } else {
                    cmdAction = "removed";
                    verb = "from";
                    
                    nexusPlayer.removeTag(tagName);
                }

                if (cmdAction.equals("unlocked")) {
                    NexusAPI.getApi().getPrimaryDatabase().saveSilent(tag);
                } else {
                    NexusAPI.getApi().getPrimaryDatabase().deleteSilent(Tag.class, tag.getId());
                }

                String playerName = NexusAPI.getApi().getPlayerManager().getNameFromUUID(uniqueId);
                sender.sendMessage(ColorUtils.color("&eYou " + cmdAction + " the tag " + tag.getDisplayName() + " &e" + verb + " the player &b" + playerName));
            });
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.color("&cOnly players can use that command."));
            return true;
        }

        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (nexusPlayer == null) {
            player.sendMessage(ColorUtils.color("&cPlease wait for your data to load before using this command."));
            return true;
        }

        Set<String> unlockedTags = nexusPlayer.getTags();
        if (args[0].equalsIgnoreCase("list")) {
            if (!unlockedTags.isEmpty()) {
                nexusPlayer.sendMessage("&eList of available tags...");
                for (String rawTag : unlockedTags) {
                    Tag tag = new Tag(null, rawTag, 0);
                    nexusPlayer.sendMessage(" &8- &e" + tag.getName() + " " + tag.getDisplayName());
                }
            } else {
                nexusPlayer.sendMessage("&cYou have no tags unlocked.");
            }
        } else if (args[0].equalsIgnoreCase("set")) {
            if (!(args.length > 1)) {
                nexusPlayer.sendMessage("&cYou must provide a tag name.");
                return true;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String tagName = sb.substring(0, sb.length() - 1);

            if (!nexusPlayer.isTagUnlocked(tagName)) {
                nexusPlayer.sendMessage("&cYou do not have a tag with that name.");
                return true;
            }

            nexusPlayer.setActiveTag(tagName);
            nexusPlayer.sendMessage("&eYou set your tag to " + nexusPlayer.getActiveTag().getDisplayName());
        } else if (args[0].equalsIgnoreCase("reset")) {
            nexusPlayer.setActiveTag(null);
            nexusPlayer.sendMessage("&eYou reset your tag.");
        }

        return true;
    }
}
