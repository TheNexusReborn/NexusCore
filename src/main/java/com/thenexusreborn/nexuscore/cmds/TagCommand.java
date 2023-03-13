package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.NexusProfile;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.stats.StatOperator;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

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
            
            NexusProfile profile = SpigotUtils.getProfileFromCommand(sender, args[1]);
            if (profile == null) return true;
    
            String cmdAction, verb, timestamp;
            if (args[0].equalsIgnoreCase("unlock")) {
                profile.getTags().add(new Tag(profile.getUniqueId(), tagName, System.currentTimeMillis()));
                cmdAction = "unlocked";
                verb = "for";
                timestamp = System.currentTimeMillis() + "";
            } else {
                profile.getTags().remove(tagName);
                cmdAction = "removed";
                verb = "from";
                timestamp = "";
            }
            
            NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> {
                if (cmdAction.equals("unlocked")) {
                    NexusAPI.getApi().getPrimaryDatabase().saveSilent(profile.getTags().get(tagName));
                } else {
                    NexusAPI.getApi().getPrimaryDatabase().deleteSilent(Tag.class, profile.getTags().get(tagName).getId());
                }
            });

            NexusAPI.getApi().getNetworkManager().send("updatetag", profile.getUniqueId().toString(), args[0], tagName, timestamp);
    
            sender.sendMessage(MCUtils.color("&eYou " + cmdAction + " the tag " + new Tag(null, tagName, 0).getDisplayName() + " &e" + verb + " the player &b" + profile.getName()));
            return true;
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MCUtils.color("&cOnly players can use that command."));
            return true;
        }
    
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (nexusPlayer == null) {
            player.sendMessage(MCUtils.color("&cPlease wait for your data to load before using this command."));
            return true;
        }
    
        Set<String> unlockedTags = nexusPlayer.getTags().findAll();
        if (args[0].equalsIgnoreCase("list")) {
            if (unlockedTags.size() > 0) {
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
            
            if (!nexusPlayer.getTags().isUnlocked(tagName)) {
                nexusPlayer.sendMessage("&cYou do not have a tag with that name.");
                return true;
            }
            
            nexusPlayer.getTags().setActive(tagName);
            nexusPlayer.changeStat("tag", tagName, StatOperator.SET).push();
            nexusPlayer.sendMessage("&eYou set your tag to " + nexusPlayer.getTags().getActive().getDisplayName());
            NexusAPI.getApi().getNetworkManager().send("updatetag", nexusPlayer.getUniqueId().toString(), "set", tagName);
            pushTagChange(nexusPlayer);
        } else if (args[0].equalsIgnoreCase("reset")) {
            nexusPlayer.changeStat("tag", "null", StatOperator.SET).push();
            nexusPlayer.getTags().setActive(null);
            nexusPlayer.sendMessage("&eYou reset your tag.");
            NexusAPI.getApi().getNetworkManager().send("updatetag", nexusPlayer.getUniqueId().toString(), "reset");
            pushTagChange(nexusPlayer);
        } 
        
        return true;
    }
    
    private void pushTagChange(NexusPlayer player) {
        NexusAPI.getApi().getScheduler().runTaskAsynchronously(() -> NexusAPI.getApi().getPrimaryDatabase().saveSilent(player.getStatValue("tag").getAsString()));
    }
}
