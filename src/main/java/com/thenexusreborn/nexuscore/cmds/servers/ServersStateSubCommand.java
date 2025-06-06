package com.thenexusreborn.nexuscore.cmds.servers;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class ServersStateSubCommand extends ServersSubCommand {
    public ServersStateSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "state", "Gets the state of the server. This is the raw data though", "s");
    }

    @Override
    protected void handle(CommandSender sender, NexusServer server) {
        sender.sendMessage(MsgType.INFO.format("State for %v", name));
        sender.sendMessage(StarColors.color(MsgType.INFO.getVariableColor() + server.getState()));
    }
}
