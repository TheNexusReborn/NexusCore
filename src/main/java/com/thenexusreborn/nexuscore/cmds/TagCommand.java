package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.function.Consumer;

public class TagCommand implements CommandExecutor {
    
    private NexusCore plugin;
    
    public TagCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color("&cUsage: /tag <list|set|reset> [tagname]"));
            return true;
        }
    
    
        TagManager tagManager = NexusAPI.getApi().getTagManager();
        
        if (args[0].equalsIgnoreCase("unlock")) {
            Rank senderRank = MCUtils.getSenderRank(plugin, sender);
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
    
            if (!(args.length > 2)) {
                sender.sendMessage(MCUtils.color("&cUsage: /tag unlock <player> <tagName>"));
                return true;
            }
    
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String tagName = sb.substring(0, sb.length() - 1);
            Tag tag = tagManager.getTag(tagName);
            if (tag == null) {
                sender.sendMessage(MCUtils.color("&cThe name of the tag you provided is not valid."));
                return true;
            }
    
            Consumer<NexusPlayer> action = nexusPlayer -> {
                nexusPlayer.unlockTag(tag);
                
                NexusAPI.getApi().getThreadFactory().runAsync(() -> {
                    try (Connection connection = NexusAPI.getApi().getConnection(); Statement statement = connection.createStatement()) {
                        String unlockedTags = NexusAPI.getApi().getDataManager().convertTags(nexusPlayer);
                        statement.executeUpdate("update players set unlockedTags='{tags}' where uuid='{uuid}';".replace("{tags}", unlockedTags).replace("{uuid}", nexusPlayer.getUniqueId().toString()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                
                sender.sendMessage(MCUtils.color("&eYou unlocked the tag " + tag.getDisplayName() + " &efor player &b" + nexusPlayer.getName()));
            };
    
            try {
                UUID uuid = UUID.fromString(args[1]);
                NexusAPI.getApi().getPlayerManager().getNexusPlayerAsync(uuid, action);
            } catch (Exception e) {
                NexusAPI.getApi().getPlayerManager().getNexusPlayerAsync(args[1], action);
            }
            
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
        
        if (args[0].equalsIgnoreCase("list")) {
            nexusPlayer.sendMessage("&eList of tags registered...");
            for (Tag tag : tagManager.getTags().values()) {
                String status;
                if (nexusPlayer.isTagUnlocked(tag)) {
                    status = "&aUnlocked";
                } else {
                    status = "&cLocked";
                }
                nexusPlayer.sendMessage(" &8- &e" + tag.getName() + " " + tag.getDisplayName() + " " + status);
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
            Tag tag = tagManager.getTag(tagName);
            if (tag == null) {
                nexusPlayer.sendMessage("&cThe name of the tag you provided is not valid.");
                return true;
            }
            
            if (!nexusPlayer.isTagUnlocked(tag)) {
                nexusPlayer.sendMessage("&cYou have not unlocked that tag yet.");
                return true;
            }
            
            nexusPlayer.setTag(tag);
            nexusPlayer.sendMessage("&eYou set your tag to " + tag.getDisplayName());
            pushTagChange(nexusPlayer);
        } else if (args[0].equalsIgnoreCase("reset")) {
            nexusPlayer.setTag(null);
            nexusPlayer.sendMessage("&eYou reset your tag.");
            pushTagChange(nexusPlayer);
        } 
        
        return true;
    }
    
    private void pushTagChange(NexusPlayer player) {
        NexusAPI.getApi().getThreadFactory().runAsync(() -> {
            try (Connection connection = NexusAPI.getApi().getConnection(); Statement statement = connection.createStatement()) {
                String tag;
                if (player.getTag() == null) {
                    tag = "NULL";
                } else {
                    tag = player.getTag().getName();
                }
                statement.executeUpdate("update players set tag='{tag}' where uuid='{uuid}'".replace("{tag}", tag).replace("{uuid}", player.getUniqueId().toString()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
