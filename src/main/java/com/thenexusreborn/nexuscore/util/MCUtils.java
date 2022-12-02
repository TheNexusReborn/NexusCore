package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class MCUtils {
    
    public static final Map<String, Integer> nameToTicks = new LinkedHashMap<>();
    public static final int ticksPerDay = 24000;
    public static final int ticksPerHour = ticksPerDay / 24;
    public static final double ticksPerMinute = ticksPerHour / 60D;
    public static final double ticksPerSecond = ticksPerMinute / 60D;
    
    static {
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
        return Math.min((double)Math.round(MinecraftServer.getServer().recentTps[0] * 100.0) / 100.0, 20.0);
    }
    
    public static NexusPlayer getPlayerFromInput(String input) {
        NexusPlayer player;
        try {
            UUID uuid = UUID.fromString(input);
            player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
            //TODO Need to return a cached profile, this system kind of needs to be reworked
        } catch (Exception e) {
            player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(input);
        }
        
        return player;
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
        return new DecimalFormat("#.#").format(number);
    }
    
    public static String color(String uncolored) {
        return ChatColor.translateAlternateColorCodes('&', uncolored);
    }
    
    public static void debugSender(CommandSender sender, String message) {
        sender.sendMessage(MCUtils.color("&7&o" + message));
    }
    
    public static String getWorldTimeAs24Hr(World world) {
        long[] worldTimeBreakdown = getWorldTimeBreakdown(world);
        long totalHours = worldTimeBreakdown[1];
        long totalMinutes = worldTimeBreakdown[2];
        long totalSeconds = worldTimeBreakdown[3];
        
        String hours = formatTimeUnit(totalHours), minutes = formatTimeUnit(totalMinutes), seconds = formatTimeUnit(totalSeconds);
        return hours + ":" + minutes + ":" + seconds;
    }
    
    public static String getWorldTimeAs12Hr(World world) {
        long[] worldTimeBreakdown = getWorldTimeBreakdown(world);
        long totalHours = worldTimeBreakdown[1];
        long totalMinutes = worldTimeBreakdown[2];
        long totalSeconds = worldTimeBreakdown[3];
    
        String meridian;
        if (totalHours < 12) {
            meridian = "am";
        } else if (totalHours >= 12) {
            meridian = "pm";
        } else {
            throw new IllegalArgumentException("Invalid time format: Total hours is greater than 24.");
        }
        
        if (totalHours > 12) {
            totalHours = totalHours - 12;
        }
        
        String hours = formatTimeUnit(totalHours), minutes = formatTimeUnit(totalMinutes), seconds = formatTimeUnit(totalSeconds) + " " + meridian;
        return hours + ":" + minutes + ":" + seconds;
    }
    
    private static long[] getWorldTimeBreakdown(World world) {
        long worldTime = world.getTime();
        double totalTicks = worldTime + 6000;
        long totalDays = (long) (totalTicks / ticksPerDay);
        totalTicks = totalTicks - (ticksPerDay * totalDays);
        long totalHours = (long) (totalTicks / ticksPerHour);
        totalTicks = totalTicks - (ticksPerHour * totalHours);
        long totalMinutes = (long) (totalTicks / ticksPerMinute);
        totalTicks = totalTicks - (ticksPerMinute * totalMinutes);
        long totalSeconds = (long) (totalTicks / ticksPerSecond);
        return new long[]{totalDays, totalHours, totalMinutes, totalSeconds};
    }
    
    private static String formatTimeUnit(long time) {
        if (time == 0) {
            return "00";
        } else if (time < 10) {
            return "0" + time;
        } else {
            return time + "";
        }
    }
    
    public static World getWorld(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return Bukkit.getWorld(ServerProperties.getLevelName());
        } else if (sender instanceof Player) {
            return ((Player) sender).getWorld();
        }
        return null;
    }
}
