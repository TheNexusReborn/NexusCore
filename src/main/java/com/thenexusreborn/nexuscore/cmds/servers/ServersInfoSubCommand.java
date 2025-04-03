package com.thenexusreborn.nexuscore.cmds.servers;

import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.api.server.VirtualServer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ServersInfoSubCommand extends ServersSubCommand {
    public ServersInfoSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "info", "Gets information about a server", "i");
    }
    
    protected void handle(CommandSender sender, NexusServer server) {
        sender.sendMessage(MsgType.INFO.format("Server Info for %v", name));
        sender.sendMessage(MsgType.INFO.format("Type: %v", server.getType().name().toLowerCase()));
        sender.sendMessage(MsgType.INFO.format("Mode: %v", server.getMode()));
        sender.sendMessage(MsgType.INFO.format("Players: %v/%v", server.getPlayers().size(), server.getMaxPlayers()));
        if (server instanceof VirtualServer virtualServer) {
            sender.sendMessage(MsgType.INFO.format("Parent: %v", virtualServer.getParentServer().getName()));
        } else if (server instanceof InstanceServer instanceServer) {
            sender.sendMessage(MsgType.INFO.format("Primary: %v", instanceServer.getPrimaryVirtualServer().getName()));
            List<String> childServerNames = new ArrayList<>();
            instanceServer.getChildServers().forEach(child -> childServerNames.add(child.getName()));
            sender.sendMessage(MsgType.INFO.format("Children: %v", childServerNames));
        }
    }
}
