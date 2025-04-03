package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusAPI;
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

        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (nexusPlayer == null) {
            player.sendMessage(StarColors.color(MsgType.WARN + "Could not get your profile data."));
            return true;
        }

        double credits = nexusPlayer.getBalance().getCredits();
        double nexites = nexusPlayer.getBalance().getNexites();
        double xp = nexusPlayer.getExperience().getLevelXp();

        DecimalFormat format = new DecimalFormat("0.#");
        nexusPlayer.sendMessage(MsgType.INFO + "Your balances");
        nexusPlayer.sendMessage("  &6&l> &3Credits &f" + format.format(credits));
        nexusPlayer.sendMessage("  &6&l> &9Nexites &f" + format.format(nexites));
        nexusPlayer.sendMessage("  &6&l> &2Experience &f" + format.format(xp));
        return true;
    }
}
