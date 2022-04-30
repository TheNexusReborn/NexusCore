package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {
    
    private NexusCore plugin;
    
    public ReplyCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color("&cOnly players may use that command."));
            return true;
        }
    
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color("&cUsage: /reply <text>"));
            return true;
        }
    
        Player senderPlayer = (Player) sender;
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
    
        player.sendMessage("&6&l>> &d&lPRIVATE &dto " + target.getRank().getColor() + target.getName() + "&8: &5" + sb);
        target.sendMessage("&6&l>> &d&lPRIVATE &dfrom " + player.getRank().getColor() + player.getName() + "&8: &5" + sb);
        player.setLastMessage(target);
        target.setLastMessage(player);
        return true;
    }
}
