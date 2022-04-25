package com.thenexusreborn.nexuscore.stats;

import java.util.*;

public final class StatRegistry {
    private StatRegistry() {
    }
    
    private static Map<String, Integer> integerStats = new HashMap<>();
    private static Map<String, Double> doubleStats = new HashMap<>();
    
    public static String formatStatName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }
    
    public static <T extends Number> Stat<T> instantiateStat(int id, String name, UUID player, T value, long created, long modified) {
        String statName = formatStatName(name);
        if (integerStats.containsKey(statName)) {
            return (Stat<T>) instantiateIntegerStat(id, name, player, (Integer) value, created, modified);
        } else if (doubleStats.containsKey(statName)) {
            return (Stat<T>) instantiateDoubleStat(id, name, player, (Double) value, created, modified);
        }
        return null;
    }
    
    public static Stat<Integer> instantiateIntegerStat(int id, String name, UUID player, int value, long created, long modified) {
        String statName = formatStatName(name);
        if (integerStats.containsKey(statName)) {
            return new Stat<>(id, player, statName, value, created, modified);
        }
        return null;
    }
    
    public static Stat<Double> instantiateDoubleStat(int id, String name, UUID player, double value, long created, long modified) {
        String statName = formatStatName(name);
        if (doubleStats.containsKey(statName)) {
            return new Stat<>(id, player, statName, value, created, modified);
        }
        return null;
    }
    
    public static Stat<? extends Number> createStat(String name, UUID player, long timestamp) {
        String statName = formatStatName(name);
        if (integerStats.containsKey(statName)) {
            return createIntegerStat(name, player, timestamp);
        } else if (doubleStats.containsKey(statName)) {
            return createDoubleStat(name, player, timestamp);
        }
        return null;
    }
    
    public static void registerIntegerStat(String name, int defaultValue) {
        String statName = formatStatName(name);
        if (integerStats.containsKey(statName) || doubleStats.containsKey(statName)) {
            throw new IllegalArgumentException("A stat with the name " + name + " is already registered");
        }
        integerStats.put(formatStatName(name), defaultValue);
    }
    
    public static Stat<Integer> createIntegerStat(String name, UUID player, long timestamp) {
        String statName = formatStatName(name);
        if (integerStats.containsKey(statName)) {
            return new Stat<>(player, statName, integerStats.get(statName), timestamp);
        }
        return null;
    }
    
    public static void registerDoubleStat(String name, double defaultValue) {
        String statName = formatStatName(name);
        if (integerStats.containsKey(statName) || doubleStats.containsKey(statName)) {
            throw new IllegalArgumentException("A stat with the name " + name + " is already registered");
        }
        doubleStats.put(formatStatName(name), defaultValue);
    }
    
    public static Stat<Double> createDoubleStat(String name, UUID player, long timestamp) {
        String statName = formatStatName(name);
        if (doubleStats.containsKey(statName)) {
            return new Stat<>(player, statName, doubleStats.get(statName), timestamp);
        }
        return null;
    }
    
    public static boolean isValidStat(String name) {
        return integerStats.containsKey(formatStatName(name)) || doubleStats.containsKey(formatStatName(name));
    }
    
    public static boolean isIntegerStat(String name) {
        return integerStats.containsKey(formatStatName(name));
    }
    
    public static boolean isDoubleStat(String name) {
        return doubleStats.containsKey(formatStatName(name));
    }
    
    public static List<String> getStats() {
        ArrayList<String> statNames = new ArrayList<>();
        statNames.addAll(integerStats.keySet());
        statNames.addAll(doubleStats.keySet());
        return statNames;
    }
}
