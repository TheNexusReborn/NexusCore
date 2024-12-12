package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BotCommand implements CommandExecutor {
    
    private NexusCore plugin;

    public BotCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            MsgType.WARN.send(sender, "Only Admins or higher can use that command.");
            return true;
        }
        
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a sub command.");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("start")) {
            if (plugin.getNexusBot().isStarted()) {
                MsgType.WARN.send(sender, "The bot is already running");
                return true;
            }
            
            MsgType.INFO.send(sender, "Starting the Nexus Discord Bot...");
            plugin.getNexusBot().start();
            if (!plugin.getNexusBot().isStarted()) {
                MsgType.ERROR.send(sender, "The bot failed to start");
                return true;
            } 
            
            MsgType.SUCCESS.send(sender, "The Nexus Discord Bot has been started successfully");
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!plugin.getNexusBot().isStarted()) {
                MsgType.WARN.send(sender, "The bot is not running");
                return true;
            }
            
            MsgType.INFO.send(sender, "Stopping the Nexus Discord Bot...");
            plugin.getNexusBot().shutdown();
            MsgType.SUCCESS.send(sender, "The Neuxs Discord Bot has been stopped successfully");
        }
        
        return true;
    }
}
