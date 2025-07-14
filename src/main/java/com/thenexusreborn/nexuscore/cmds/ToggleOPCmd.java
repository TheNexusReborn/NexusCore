package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class ToggleOPCmd extends NexusCommand<NexusCore> {
    public ToggleOPCmd(NexusCore plugin) {
        super(plugin, "toggleop", "Toggling of OP status", Rank.NEXUS);
        this.playerOnly = true;
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (senderRank != Rank.NEXUS) {
            return true;
        }
        
        if (sender.isOp()) {
            sender.setOp(false);
            MsgType.INFO.send(sender, "You are no longer a server operator.");
        } else {
            sender.setOp(true);
            MsgType.INFO.send(sender, "You are now a server operator.");
        }
        
        return true;
    }
}
