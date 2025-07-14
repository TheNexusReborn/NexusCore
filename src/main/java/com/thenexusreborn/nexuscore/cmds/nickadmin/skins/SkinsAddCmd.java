package com.thenexusreborn.nexuscore.cmds.nickadmin.skins;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class SkinsAddCmd extends SubCommand<NexusCore> {
    public SkinsAddCmd(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 1, "add", "Adds one or more skins to the random skins list", Rank.ADMIN, "a");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Set<String> randomSkins = NexusReborn.getRandomSkins();
        
        for (String arg : args) {
            String skin = arg.toLowerCase();
            if (!randomSkins.contains(skin)) {
                randomSkins.add(skin);
                MsgType.INFO.send(sender, "You added %v to the list of random skins.", skin);
            } else {
                MsgType.WARN.send(sender, "That skin is already on the list.");
                return true;
            }
        }
        
        return true;
    }
}
