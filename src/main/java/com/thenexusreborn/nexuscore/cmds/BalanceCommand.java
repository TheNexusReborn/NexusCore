package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class BalanceCommand extends NexusCommand<NexusCore> {
    public BalanceCommand(NexusCore plugin) {
        super(plugin, "balance", "", Rank.MEMBER, "bal");
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }

        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (nexusPlayer == null) {
            player.sendMessage(StarColors.color(MsgType.WARN + "Could not get your profile data."));
            return true;
        }
        
        DecimalFormat format = new DecimalFormat("0.#");

        String creditsLine = "  &6&l> &3Credits &f" + format.format(nexusPlayer.getBalance().getCredits());
        String nexitesLine = "  &6&l> &9Nexites &f" + format.format(nexusPlayer.getBalance().getNexites());
        String levelLine = "  &6&l> &2Level: &f" + format.format(nexusPlayer.getExperience().getLevel());
        String xpLine = "  &6&l> &aExperience: &f" + format.format(nexusPlayer.getExperience().getLevelXp());
        
        if (nexusPlayer.isNicked()) {
            creditsLine += " &8(&7" + format.format(nexusPlayer.getTrueBalance().getCredits()) + "&8)";
            nexitesLine += " &8(&7" + format.format(nexusPlayer.getTrueBalance().getNexites()) + "&8)";
            levelLine += " &8(&7" + format.format(nexusPlayer.getTrueExperience().getLevel()) + "&8)";
            xpLine += " &8(&7" + format.format(nexusPlayer.getTrueExperience().getLevelXp()) + "&8)";
        }
       
        nexusPlayer.sendMessage(MsgType.INFO + "Your balances");
        nexusPlayer.sendMessage(creditsLine);
        nexusPlayer.sendMessage(nexitesLine);
        nexusPlayer.sendMessage(levelLine);
        nexusPlayer.sendMessage(xpLine);
        return true;
    }
}
