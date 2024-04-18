package com.thenexusreborn.nexuscore.util;

import com.stardevllc.starcore.color.ColorUtils;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.util.Constants;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MCUtils {
    
    public static final Map<String, Integer> nameToTicks = new LinkedHashMap<>();
    public static final int ticksPerDay = 24000;
    public static final int ticksPerHour = ticksPerDay / 24;
    public static final double ticksPerMinute = ticksPerHour / 60D;
    public static final double ticksPerSecond = ticksPerMinute / 60D;
    
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
        
        nameToTicks.put("sunrise", 23000);
        nameToTicks.put("dawn", 23000);
    
        nameToTicks.put("daystart", 0);
        nameToTicks.put("day", 0);
    
        nameToTicks.put("morning", 1000);
    
        nameToTicks.put("midday", 6000);
        nameToTicks.put("noon", 6000);
    
        nameToTicks.put("afternoon", 9000);
    
        nameToTicks.put("sunset", 12000);
        nameToTicks.put("dusk", 12000);
        nameToTicks.put("sundown", 12000);
        nameToTicks.put("nightfall", 12000);
    
        nameToTicks.put("nightstart", 14000);
        nameToTicks.put("night", 14000);
    
        nameToTicks.put("midnight", 18000);
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
    
    public static Rank getSenderRank(NexusCore plugin, CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return Rank.ADMIN;
        } else if (sender instanceof Player player) {
            return NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId()).getRank();
        }
        return Rank.MEMBER;
    }
    
    public static String formatNumber(Number number) {
        return new DecimalFormat(Constants.NUMBER_FORMAT).format(number);
    }
    
    public static void debugSender(CommandSender sender, String message) {
        sender.sendMessage(ColorUtils.color("&7&o" + message));
    }
}
