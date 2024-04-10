package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class BalanceCommand implements CommandExecutor {
    private NexusCore plugin;

    public BalanceCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }

        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (nexusPlayer == null) {
            player.sendMessage(ColorUtils.color(MsgType.WARN + "Could not get your profile data."));
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
