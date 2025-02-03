package com.thenexusreborn.nexuscore.util;

import com.stardevllc.colors.StarColors;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public final class MCUtils {
    
    private static Class<?> minecraftServerClass;
    private static Method getServerMethod;
    private static Field recentTpsField;

    static {
        try {
            minecraftServerClass = Class.forName("net.minecraft.server.v1_8_R3.MinecraftServer");
            getServerMethod = minecraftServerClass.getDeclaredMethod("getServer");
            getServerMethod.setAccessible(true);
            recentTpsField = minecraftServerClass.getDeclaredField("recentTps");
            recentTpsField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getRecentTps() {
        try {
            Object serverInstance = getServerMethod.invoke(null);
            Object recentTps = recentTpsField.get(serverInstance);
            return Math.min(Math.round((double) Array.get(recentTps, 0) * 100.0) / 100.0, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public static Rank getSenderRank(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return Rank.CONSOLE;
        } else if (sender instanceof Player player) {
            return NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId()).getRank();
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
