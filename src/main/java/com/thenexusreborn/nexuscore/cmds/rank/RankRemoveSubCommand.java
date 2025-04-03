package com.thenexusreborn.nexuscore.cmds.rank;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.player.PlayerRanks;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class RankRemoveSubCommand extends RankModifySubcommand {
    public RankRemoveSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "remove", "Remove a rank from a player", "r");
    }

    @Override
    protected void handle(CommandSender sender, Rank rank, long expire, PlayerRanks targetRanks, String targetName, String rankName, long time) {
        try {
            targetRanks.remove(rank);
        } catch (Exception e) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "There was a problem removing the rank: " + e.getMessage()));
            return;
        }
        sender.sendMessage(StarColors.color("&eYou removed the rank " + rankName + " &efrom &b" + targetName));
    }
}
