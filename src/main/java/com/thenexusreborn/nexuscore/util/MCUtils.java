package com.thenexusreborn.nexuscore.util;

import com.stardevllc.starcore.api.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.nexuscore.reflection.ServerReflection;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public final class MCUtils {
    
    private static final ServerReflection SERVER_REFLECTION = new ServerReflection();
    
    @Deprecated
    public static double getRecentTps() {
        return SERVER_REFLECTION.getRecentTps();
    }
    
    public static Rank getSenderRank(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return Rank.CONSOLE;
        } else if (sender instanceof Player player) {
            return NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId()).getRank();
        }
        return Rank.MEMBER;
    }
    
    public static String formatNumber(Number number) {
        return new DecimalFormat(Constants.NUMBER_FORMAT).format(number);
    }
    
    public static void debugSender(CommandSender sender, String message) {
        sender.sendMessage(StarColors.color("&7&o" + message));
    }
}
