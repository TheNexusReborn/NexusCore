package com.thenexusreborn.nexuscore.cmds.nickadmin.blacklist;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class BlacklistAddCmd extends SubCommand<NexusCore> {
    public BlacklistAddCmd(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 1, "add", "Adds one or more players to the blacklist", Rank.ADMIN, "a");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Set<String> nicknameBlacklist = NexusReborn.getNicknameBlacklist();
        
        for (String arg : args) {
            String name = arg.toLowerCase();
            if (!nicknameBlacklist.contains(name)) {
                nicknameBlacklist.add(name);
                MsgType.INFO.send(sender, "You added %v to the blacklist.", name);
            } else {
                MsgType.WARN.send(sender, "That name is already on the blacklist");
                return true;
            }
        }
        
        return true;
    }
}
