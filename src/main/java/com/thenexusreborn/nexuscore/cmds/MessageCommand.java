package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(MCUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        if (!(args.length > 1)) {
            sender.sendMessage(MCUtils.color("&cUsage: /message <player> <text>"));
            return true;
        }
    
        NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(senderPlayer.getUniqueId());
        
        NexusPlayer target;
        try {
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(UUID.fromString(args[0]));
        } catch (Exception e) {
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(args[0]);
        }
        
        if (target == null) {
            player.sendMessage("&cThat player is not online.");
            return true;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        
        player.sendMessage("&6&l>> &2&lPRIVATE &dto " + target.getRank().getColor() + target.getName() + "&8: &a" + sb);
        target.sendMessage("&6&l>> &2&lPRIVATE &dfrom " + player.getRank().getColor() + player.getName() + "&8: &a" + sb);
        player.setLastMessage(target);
        target.setLastMessage(player);
        return true;
    }
}
