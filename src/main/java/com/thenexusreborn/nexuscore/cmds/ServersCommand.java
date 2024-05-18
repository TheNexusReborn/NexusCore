package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.color.ColorHandler;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.api.server.VirtualServer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ServersCommand implements CommandExecutor {
    
    private NexusCore plugin;

    public ServersCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(ColorHandler.getInstance().color("&cYou do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(MsgType.WARN.format("You must provide a sub command."));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(MsgType.INFO.format("List of all servers..."));
            for (NexusServer server : NexusAPI.getApi().getServerRegistry()) {
                String name = server.getName();
                String type = server.getType().name().toLowerCase();
                String mode = server.getMode();
                
                int players = server.getPlayers().size();
                int maxPlayers = server.getMaxPlayers();
                
                sender.sendMessage(MsgType.INFO.format("Name: %v  Type: %v  Mode: %v  %v/%v", name, type, mode, players, maxPlayers));
            }
        } else {
            NexusServer server = NexusAPI.getApi().getServerRegistry().get(args[0]);
            if (server == null) {
                sender.sendMessage(MsgType.WARN.format("Invalid server name %v", args[0]));
                return true;
            }

            String name = server.getName();
            String type = server.getType().name().toLowerCase();
            String mode = server.getMode();
            
            int players = server.getPlayers().size();
            int maxPlayers = server.getMaxPlayers();
            
            // /server <name>
            
            if (args.length == 1) {
                sender.sendMessage(MsgType.INFO.format("Server Info for %v", name));
                sender.sendMessage(MsgType.INFO.format("Type: %v", type));
                sender.sendMessage(MsgType.INFO.format("Mode: %v", mode));
                sender.sendMessage(MsgType.INFO.format("Players: %v/%v", players, maxPlayers));
                if (server instanceof VirtualServer virtualServer) {
                    sender.sendMessage(MsgType.INFO.format("Parent: %v", virtualServer.getParentServer().getName()));
                } else if (server instanceof InstanceServer instanceServer) {
                    sender.sendMessage(MsgType.INFO.format("Primary: %v", instanceServer.getPrimaryVirtualServer().getName()));
                    List<String> childServerNames = new ArrayList<>();
                    instanceServer.getChildServers().forEach(child -> childServerNames.add(child.getName()));
                    sender.sendMessage(MsgType.INFO.format("Children: %v", childServerNames));
                }
            } else {
                // /server <name> state
                if (args[1].equalsIgnoreCase("state")) {
                    sender.sendMessage(MsgType.INFO.format("State for %v", name));
                    sender.sendMessage(ColorHandler.getInstance().color(MsgType.INFO.getVariableColor() + server.getState()));
                } else if (args[1].equalsIgnoreCase("players")) {
                    Set<String> serverPlayers = new HashSet<>();
                    for (UUID uuid : server.getPlayers()) {
                        String playerName;
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            playerName = player.getName();
                        } else {
                            playerName = NexusAPI.getApi().getPlayerManager().getNameFromUUID(uuid);
                        }
                        serverPlayers.add(playerName);
                    }

                    sender.sendMessage(MsgType.INFO.format("Players on %v", name));
                    sender.sendMessage(ColorHandler.getInstance().color(MsgType.INFO.getVariableColor() + serverPlayers));
                }
            }
        }
        
        return true;
    }
}
