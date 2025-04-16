package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageCommand extends NexusCommand<NexusCore> {

    public MessageCommand(NexusCore plugin) {
        super(plugin, "message", "", Rank.MEMBER, "msg", "tell", "whisper", "w");
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(StarColors.color("&cOnly players may use that command."));
            return true;
        }

        if (!(args.length > 1)) {
            sender.sendMessage(StarColors.color("&cUsage: /message <player> <text>"));
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
        
        if (target.getToggleValue("vanish")) {
            if (target.getRank().ordinal() < player.getRank().ordinal()) {
                player.sendMessage("&cThat player is not online.");
                return true;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        player.sendMessage("&6&l>> &2&lPRIVATE &2to " + target.getEffectiveRank().getColor() + target.getName() + "&8: &a" + sb);
        target.sendMessage("&6&l>> &2&lPRIVATE &2from " + player.getEffectiveRank().getColor() + player.getName() + "&8: &a" + sb);
        player.setLastMessage(target);
        target.setLastMessage(player);
        return true;
    }
}
