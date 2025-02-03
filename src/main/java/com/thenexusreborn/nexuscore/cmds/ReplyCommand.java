package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.colors.StarColors;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand extends NexusCommand<NexusCore> {

    public ReplyCommand(NexusCore plugin) {
        super(plugin, "reply", "", Rank.MEMBER, "r");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(StarColors.color("&cOnly players may use that command."));
            return true;
        }
    
        if (!(args.length > 0)) {
            sender.sendMessage(StarColors.color("&cUsage: /reply <text>"));
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
    
        player.sendMessage("&6&l>> &2&lPRIVATE &2to " + target.getRank().getColor() + target.getName() + "&8: &a" + sb);
        target.sendMessage("&6&l>> &2&lPRIVATE &2from " + player.getRank().getColor() + player.getName() + "&8: &a" + sb);
        player.setLastMessage(target);
        target.setLastMessage(player);
        return true;
    }
}
