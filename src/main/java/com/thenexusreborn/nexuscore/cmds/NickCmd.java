package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NickCmd extends NexusCommand<NexusCore> {

    public NickCmd(NexusCore plugin) {
        super(plugin, "nickname", "", Rank.DIAMOND, "nick");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        return true;
    }
}
