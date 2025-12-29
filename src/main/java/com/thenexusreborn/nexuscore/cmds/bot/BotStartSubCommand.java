package com.thenexusreborn.nexuscore.cmds.bot;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class BotStartSubCommand extends SubCommand<NexusCore> {
    public BotStartSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "start", "Starts the bot", Rank.ADMIN);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (plugin.getNexusBot().isStarted()) {
            MsgType.WARN.send(sender, "The bot is already running");
            return true;
        }

        MsgType.INFO.send(sender, "Starting the Nexus Discord Bot...");
        plugin.getNexusBot().start();
        if (!plugin.getNexusBot().isStarted()) {
            MsgType.ERROR.send(sender, "The bot failed to start");
            return true;
        }

        MsgType.SUCCESS.send(sender, "The Nexus Discord Bot has been started successfully");
        return true;
    }
}
