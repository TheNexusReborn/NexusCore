package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(MCUtils.color("&cOnly players may use that command."));
            return true;
        }
    
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color("&cUsage: /reply <text>"));
            return true;
        }
    
        NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(senderPlayer.getUniqueId());
        NexusPlayer target = player.getLastMessage();
        if (target == null) {
            player.sendMessage("&cYou haven't messaged anyone or they left.");
            return true;
        }
    
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
    
        player.sendMessage("&6&l>> &d&lPRIVATE &dto " + target.getRanks().get().getColor() + target.getName() + "&8: &5" + sb);
        target.sendMessage("&6&l>> &d&lPRIVATE &dfrom " + player.getRanks().get().getColor() + player.getName() + "&8: &5" + sb);
        player.setLastMessage(target);
        target.setLastMessage(player);
        return true;
    }
}
