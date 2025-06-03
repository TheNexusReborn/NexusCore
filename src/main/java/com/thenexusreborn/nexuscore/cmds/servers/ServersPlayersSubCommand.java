package com.thenexusreborn.nexuscore.cmds.servers;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ServersPlayersSubCommand extends ServersSubCommand {
    public ServersPlayersSubCommand(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, "players", "List the players of the server", "p");
    }

    @Override
    protected void handle(CommandSender sender, NexusServer server) {
        Set<String> serverPlayers = new HashSet<>();
        for (UUID uuid : server.getPlayers()) {
            String playerName;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                playerName = player.getName();
            } else {
                playerName = NexusReborn.getPlayerManager().getNameFromUUID(uuid);
            }
            serverPlayers.add(playerName);
        }

        sender.sendMessage(MsgType.INFO.format("Players on %v", server.getName()));
        sender.sendMessage(StarColors.color(MsgType.INFO.getVariableColor() + serverPlayers));
    }
}
