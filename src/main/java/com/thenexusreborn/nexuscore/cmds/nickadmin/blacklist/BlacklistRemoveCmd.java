package com.thenexusreborn.nexuscore.cmds.nickadmin.blacklist;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class BlacklistRemoveCmd extends SubCommand<NexusCore> {
    public BlacklistRemoveCmd(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 1, "remove", "Removes one or more players from the blacklist", Rank.ADMIN, "r");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Set<String> nicknameBlacklist = NexusReborn.getNicknameBlacklist();
        
        for (String arg : args) {
            String name = arg.toLowerCase();
            if (nicknameBlacklist.contains(name)) {
                nicknameBlacklist.remove(name);
                MsgType.INFO.send(sender, "You removed %v from the blacklist.", name);
            } else {
                MsgType.WARN.send(sender, "That name is not on the blacklist");
                return true;
            }
        }
        
        return true;
    }
}
