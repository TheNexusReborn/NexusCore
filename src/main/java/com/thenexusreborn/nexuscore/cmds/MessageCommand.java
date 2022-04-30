package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.command.*;

public class MessageCommand implements CommandExecutor {
    
    private NexusCore plugin;
    
    public MessageCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        return true;
    }
}
