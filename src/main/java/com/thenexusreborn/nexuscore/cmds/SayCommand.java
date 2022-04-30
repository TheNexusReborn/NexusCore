package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.command.*;

public class SayCommand implements CommandExecutor {
    
    private NexusCore plugin;
    
    public SayCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        return true;
    }
}
