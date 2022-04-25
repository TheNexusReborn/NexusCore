package com.thenexusreborn.nexuscore.util;

import org.bukkit.*;

import java.io.*;
import java.util.Properties;

/**
 * A utility class to get the values of the server.properties file
 */
public final class ServerProperties {
    private static final File SERVER_PROPERTIES_FILE = new File(".", "server.properties");
    private static final Properties SERVER_PROPERTIES;
    static {
        SERVER_PROPERTIES = new Properties();
        try {
            FileInputStream fis = new FileInputStream(SERVER_PROPERTIES_FILE);
            SERVER_PROPERTIES.load(fis);
        } catch (Exception e) {}
    }
    
    public static boolean getEnableJMXMonitoring() {
        return (boolean) SERVER_PROPERTIES.get("enable-jmx-monitoring");
    }
    
    public static int getRconPort() {
        return (int) SERVER_PROPERTIES.get("rcon.port");
    }
    
    public static boolean getEnableCommandBlock() {
        return (boolean) SERVER_PROPERTIES.get("enable-command-block");
    }
    
    public static GameMode getDefaultGameMode() {
        return GameMode.valueOf(((String) SERVER_PROPERTIES.get("gamemode")).toUpperCase());
    }
    
    public static boolean getEnableQuery() {
        return (boolean) SERVER_PROPERTIES.get("enable-query");
    }
    
    public static String getLevelName() {
        return (String) SERVER_PROPERTIES.get("level-name");
    }
    
    public static String getMotd() {
        return (String) SERVER_PROPERTIES.get("motd");
    }
    
    public static int getQueryPort() {
        return (int) SERVER_PROPERTIES.get("query.port");
    }
    
    public static boolean getPVP() {
        return (boolean) SERVER_PROPERTIES.get("pvp");
    }
    
    public static Difficulty getDifficulty() {
        return Difficulty.valueOf(((String) SERVER_PROPERTIES.get("difficulty")).toUpperCase());
    }
    
    public static int getNetworkCompressionThreshold() {
        return (int) SERVER_PROPERTIES.get("network-compression-threshold");
    }
    
    public static int getMaxTickTime() {
        return (int) SERVER_PROPERTIES.get("max-tick-time");
    }
    
    public static boolean getRequireResourcePack() {
        return (boolean) SERVER_PROPERTIES.get("require-resource-pack");
    }
    
    public static int getMaxPlayers() {
        return (int) SERVER_PROPERTIES.get("max-players");
    }
    
    public static boolean getUseNativeTransport() {
        return (boolean) SERVER_PROPERTIES.get("use-native-transport");
    }
    
    public static boolean getOnlineMode() {
        return (boolean) SERVER_PROPERTIES.get("online-mode");
    }
    
    public static boolean getEnableStatus() {
        return (boolean) SERVER_PROPERTIES.get("enable-status");
    }
    
    public static boolean getAllowFlight() {
        return (boolean) SERVER_PROPERTIES.get("allow-flight");
    }
    
    public static boolean getBroadcastRconToOps() {
        return (boolean) SERVER_PROPERTIES.get("broadcast-rcon-to-ops");
    }
    
    public static int getViewDistance() {
        return (int) SERVER_PROPERTIES.get("view-distance");
    }
    
    public static String getServerIp() {
        return (String) SERVER_PROPERTIES.get("server-ip");
    }
    
    public static String getResourcePackPrompt() {
        return (String) SERVER_PROPERTIES.get("resource-pack-prompt");
    }
    
    public static boolean getAllowNether() {
        return (boolean) SERVER_PROPERTIES.get("allow-nether");
    }
    
    public static int getServerPort() {
        return (int) SERVER_PROPERTIES.get("server-port");
    }
    
    public static boolean getEnableRcon() {
        return (boolean) SERVER_PROPERTIES.get("enable-rcon");
    }
    
    public static boolean getSyncChunkWrites() {
        return (boolean) SERVER_PROPERTIES.get("sync-chunk-writes");
    }
    
    public static int getOpPermissionLevel() {
        return (int) SERVER_PROPERTIES.get("op-permission-level");
    }
    
    public static boolean getPreventProxyConnections() {
        return (boolean) SERVER_PROPERTIES.get("prevent-proxy-connections");
    }
    
    public static boolean getHideOnlinePlayers() {
        return (boolean) SERVER_PROPERTIES.get("hide-online-players");
    }
    
    public static String getResourcePack() {
        return (String) SERVER_PROPERTIES.get("resource-pack");
    }
    
    public static int getEntityBroadcastRangePercentage() {
        return (int) SERVER_PROPERTIES.get("entity-broadcast-range-percentage");
    }
    
    public static int getSimulationDistance() {
        return (int) SERVER_PROPERTIES.get("simulation-distance");
    }
    
    public static String getRconPassword() {
        return (String) SERVER_PROPERTIES.get("rcon.password");
    }
    
    public static int getPlayerIdleTimeout() {
        return (int) SERVER_PROPERTIES.get("player-idle-timeout");
    }
    
    public static boolean getDebug() {
        return (boolean) SERVER_PROPERTIES.get("debug");
    }
    
    public static boolean getForceGamemode() {
        return (boolean) SERVER_PROPERTIES.get("force-gamemode");
    }
    
    public static int getRateLimit() {
        return (int) SERVER_PROPERTIES.get("rate-limit");
    }
    
    public static boolean getHardcore() {
        return (boolean) SERVER_PROPERTIES.get("hardcore");
    }
    
    public static boolean getWhitelist() {
        return (boolean) SERVER_PROPERTIES.get("white-list");
    }
    
    public static boolean getBroadcastConsoleToOps() {
        return (boolean) SERVER_PROPERTIES.get("broadcast-console-to-ops");
    }
    
    public static boolean getSpawnNpcs() {
        return (boolean) SERVER_PROPERTIES.get("spawn-npcs");
    }
    
    public static boolean getSpawnAnimals() {
        return (boolean) SERVER_PROPERTIES.get("spawn-animals");
    }
    
    public static int getFunctionPermissionLevel() {
        return (int) SERVER_PROPERTIES.get("function-permission-level"); 
    }
    
    public static boolean getSpawnMonsters() {
        return (boolean) SERVER_PROPERTIES.get("spawn-monsters");
    }
    
    public static boolean getEnforceWhitelist() {
        return (boolean) SERVER_PROPERTIES.get("enforce-whitelist");
    }
    
    public static String getResourcePackSha1() {
        return (String) SERVER_PROPERTIES.get("resource-pack-sha1");
    }
    
    public static int getSpawnProtection() {
        return (int) SERVER_PROPERTIES.get("spawn-protection");
    }
    
    public static int getMaxWorldSize() {
        return (int) SERVER_PROPERTIES.get("max-world-size");
    }
}
