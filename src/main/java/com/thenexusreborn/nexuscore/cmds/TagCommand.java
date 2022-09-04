package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class TagCommand implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public TagCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color("&cUsage: /tag <list|set|reset> [tagname]"));
            return true;
        }
    
        if (args[0].equalsIgnoreCase("unlock") || args[0].equalsIgnoreCase("remove")) {
            Rank senderRank = MCUtils.getSenderRank(plugin, sender);
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
    
            if (!(args.length > 2)) {
                sender.sendMessage(MCUtils.color("&cUsage: /tag " + args[0] + " <player> <tagName>"));
                return true;
            }
    
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String tagName = sb.substring(0, sb.length() - 1);
            
            NexusPlayer player;
            try {
                UUID uuid = UUID.fromString(args[1]);
                player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
            } catch (Exception e) {
                player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(args[1]);
            }
            
            if (player == null) {
                try {
                    UUID uuid = UUID.fromString(args[1]);
                    player = NexusAPI.getApi().getPlayerManager().getCachedPlayer(uuid).loadFully();
                } catch (Exception e) {
                    player = NexusAPI.getApi().getPlayerManager().getCachedPlayer(args[1]).loadFully();
                }
            }
    
            String cmdAction, verb;
            if (args[0].equalsIgnoreCase("unlock")) {
                player.unlockTag(tagName);
                cmdAction = "unlocked";
                verb = "for";
            } else {
                player.lockTag(tagName);
                cmdAction = "removed";
                verb = "from";
            }
            
            NexusAPI.getApi().getNetworkManager().send("updatetag", player.getUniqueId().toString(), args[0], tagName);
    
            NexusAPI.getApi().getPrimaryDatabase().push(player.getStat("unlockedtags"));
            sender.sendMessage(MCUtils.color("&eYou " + cmdAction + " the tag " + new Tag(tagName).getDisplayName() + " &e" + verb + " the player &b" + player.getName()));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color("&cOnly players can use that command."));
            return true;
        }
        
        Player player = (Player) sender;
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (nexusPlayer == null) {
            player.sendMessage(MCUtils.color("&cPlease wait for your data to load before using this command."));
            return true;
        }
    
        Set<String> unlockedTags = nexusPlayer.getUnlockedTags();
        if (args[0].equalsIgnoreCase("list")) {
            if (unlockedTags.size() > 0) {
                nexusPlayer.sendMessage("&eList of available tags...");
                for (String rawTag : unlockedTags) {
                    Tag tag = new Tag(rawTag);
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
             
            Tag tag = null;
            for (String unlocked : unlockedTags) {
                if (unlocked.equalsIgnoreCase(tagName)) {
                    tag = new Tag(unlocked);
                }
            }
            
            if (tag == null) {
                nexusPlayer.sendMessage("&cYou do not have a tag with that name.");
                return true;
            }
            
            nexusPlayer.setTag(tag);
            nexusPlayer.sendMessage("&eYou set your tag to " + tag.getDisplayName());
            NexusAPI.getApi().getNetworkManager().send("updatetag", nexusPlayer.getUniqueId().toString(), "set", tag.getName());
            pushTagChange(nexusPlayer);
        } else if (args[0].equalsIgnoreCase("reset")) {
            nexusPlayer.setTag(null);
            nexusPlayer.sendMessage("&eYou reset your tag.");
            NexusAPI.getApi().getNetworkManager().send("updatetag", nexusPlayer.getUniqueId().toString(), "reset");
            pushTagChange(nexusPlayer);
        } 
        
        return true;
    }
    
    private void pushTagChange(NexusPlayer player) {
        NexusAPI.getApi().getThreadFactory().runAsync(() -> NexusAPI.getApi().getPrimaryDatabase().push(player.getStat("tag")));
    }
}
