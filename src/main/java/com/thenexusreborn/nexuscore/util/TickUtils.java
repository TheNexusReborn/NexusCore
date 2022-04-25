package com.thenexusreborn.nexuscore.util;

/**
 * A utility for tick conversions
 */
public final class TickUtils {
    private TickUtils() {
    }
    
    /**
     * Converts ticks into milliseconds
     *
     * @param duration The number of ticks
     * @return The number of milliseconds represented by the ticks
     */
    public static long asMilliseconds(long duration) {
        return duration * 50;
    }
    
    /**
     * Converts ticks into seconds
     *
     * @param duration The number of ticks
     * @return The number of seconds represented by the ticks
     */
    public static int asSeconds(int duration) {
        return duration / 20;
    }
    
    /**
     * Converts ticks into minutes
     *
     * @param duration The number of ticks
     * @return The number of minutes represented by the ticks
     */
    public static int asMinutes(int duration) {
        return duration / (asSeconds(1) / 60);
    }
    
    /**
     * Converts ticks into hours
     *
     * @param duration The number of ticks
     * @return The number of hours represented by the ticks
     */
    public static int asHours(int duration) {
        return duration / (asMinutes(1) / 60);
    }
}
