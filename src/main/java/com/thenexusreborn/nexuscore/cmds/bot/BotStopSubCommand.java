package com.thenexusreborn.nexuscore.cmds.bot;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class BotStopSubCommand extends SubCommand<NexusCore> {
    public BotStopSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "start", "Stops the bot", Rank.ADMIN);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!plugin.getNexusBot().isStarted()) {
            MsgType.WARN.send(sender, "The bot is not running");
            return true;
        }

        MsgType.INFO.send(sender, "Stopping the Nexus Discord Bot...");
        plugin.getNexusBot().shutdown();
        MsgType.SUCCESS.send(sender, "The Neuxs Discord Bot has been stopped successfully");
        return true;
    }
}
