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

public class SkinsRemoveCmd extends SubCommand<NexusCore> {
    public SkinsRemoveCmd(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 1, "remove", "Removes one or more skins from the random skins list", Rank.ADMIN, "r");
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Set<String> randomSkins = NexusReborn.getRandomSkins();
        
        for (String arg : args) {
            String skin = arg.toLowerCase();
            if (randomSkins.contains(skin)) {
                randomSkins.remove(skin);
                MsgType.INFO.send(sender, "You removed %v from the list of random skins.", skin);
            } else {
                MsgType.WARN.send(sender, "That name is not on the list of random skins.");
                return true;
            }
        }
        
        return true;
    }
}
