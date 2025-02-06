package com.thenexusreborn.nexuscore.cmds.servers;

import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

public class ServersListSubCommand extends SubCommand<NexusCore> {
    public ServersListSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "list", "", Rank.ADMIN);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        sender.sendMessage(MsgType.INFO.format("List of all servers..."));
        for (NexusServer server : NexusAPI.getApi().getServerRegistry()) {
            String name = server.getName();
            String type = server.getType().name().toLowerCase();
            String mode = server.getMode();

            int players = server.getPlayers().size();
            int maxPlayers = server.getMaxPlayers();

            sender.sendMessage(MsgType.INFO.format("Name: %v  Type: %v  Mode: %v  %v/%v", name, type, mode, players, maxPlayers));
        }
        return true;
    }
}
