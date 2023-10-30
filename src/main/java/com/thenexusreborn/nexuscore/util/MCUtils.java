package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

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
        if (uncolored == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', uncolored);
    }
    
    public static void debugSender(CommandSender sender, String message) {
        sender.sendMessage(MCUtils.color("&7&o" + message));
    }
}
