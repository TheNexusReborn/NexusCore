package com.thenexusreborn.nexuscore.cmds.servers;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public abstract class ServersSubCommand extends SubCommand<NexusCore> {
    public ServersSubCommand(NexusCore plugin, ICommand<NexusCore> parent, String name, String description, String... aliases) {
        super(plugin, parent, 0, name, description, Rank.ADMIN, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a server name.");
            return true;
        }
        
        NexusServer server = NexusReborn.getServerRegistry().get(args[0]);
        if (server == null) {
            sender.sendMessage(MsgType.WARN.format("Invalid server name %v", args[0]));
            return true;
        }

        handle(sender, server);
        return true;
    }

    protected abstract void handle(CommandSender sender, NexusServer server);
}
