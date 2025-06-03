package com.thenexusreborn.nexuscore.cmds.rank;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.player.PlayerRanks;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import org.bukkit.command.CommandSender;

public class RankSetSubCommand extends RankModifySubcommand {
    public RankSetSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "set", "Clear and set a player's rank", "s");
    }

    @Override
    protected void handle(CommandSender sender, Rank rank, long expire, PlayerRanks targetRanks, String targetName, String rankName, long time) {
        targetRanks.set(rank, expire);
        String message = "&eYou set &b" + targetName + "'s &erank to " + rankName;
        if (time > -1) {
            message += " &efor &b" + Constants.PUNISHMENT_TIME_FORMAT.format(time);
        }
        sender.sendMessage(StarColors.color(message));
    }
}
