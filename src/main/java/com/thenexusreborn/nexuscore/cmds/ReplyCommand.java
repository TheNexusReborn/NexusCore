package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.command.*;

public class ReplyCommand implements CommandExecutor {
    
    private NexusCore plugin;
    
    public ReplyCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        return true;
    }
}
