package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.helper.MojangHelper;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class NexusAdminCmd implements CommandExecutor {
    
    private NexusCore plugin;
    private NexusAPI nexusAPI;
    
    public NexusAdminCmd(NexusCore plugin) {
        this.plugin = plugin;
        this.nexusAPI = NexusAPI.getApi();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank rank = MCUtils.getSenderRank(plugin, sender);
        if (rank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub-command"));
            return true;
        }
        
        return true;
    }
}