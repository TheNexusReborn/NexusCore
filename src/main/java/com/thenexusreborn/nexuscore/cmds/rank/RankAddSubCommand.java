package com.thenexusreborn.nexuscore.cmds.rank;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.player.PlayerRanks;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class RankAddSubCommand extends RankModifySubcommand {
    public RankAddSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "add", "Add a rank to the player", "a");
    }

    @Override
    protected void handle(CommandSender sender, Rank rank, long expire, PlayerRanks targetRanks, String targetName, String rankName, long time) {
        try {
            targetRanks.add(rank, expire);
        } catch (Exception e) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "There was a problem setting the rank: " + e.getMessage()));
            return;
        }
        String message = "&eYou added the rank " + rankName + " &eto the player &b" + targetName;
        if (time > -1) {
            message += " &efor &b" + Constants.PUNISHMENT_TIME_FORMAT.format(time);
        }
        sender.sendMessage(StarColors.color(message));
    }
}
