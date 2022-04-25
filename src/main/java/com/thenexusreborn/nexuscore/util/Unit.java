package com.thenexusreborn.nexuscore.util;

import java.util.concurrent.TimeUnit;

/**
 * A utility to match different time formats and any aliases to the actual type. 
 */
public enum Unit {
    SECONDS("seconds", "second", "s") {
        public long convertTime(long rawLength) {
            return TimeUnit.SECONDS.toMillis(rawLength);
        }
    },

    MINUTES("minutes", "minute", "min", "m") {
        public long convertTime(long rawLength) {
            return TimeUnit.MINUTES.toMillis(rawLength);
        }
    },

    HOURS("hours", "hour", "h") {
        public long convertTime(long rawLength) {
            return TimeUnit.HOURS.toMillis(rawLength);
        }
    },

    DAYS("days", "day", "d") {
        public long convertTime(long rawLength) {
            return TimeUnit.DAYS.toMillis(rawLength);
        }
    },

    WEEKS("weeks", "week", "w") {
        public long convertTime(long rawLength) {
            return TimeUnit.DAYS.toMillis(rawLength) * 7;
        }
    },

    MONTHS("months", "month", "mo") {
        public long convertTime(long rawLength) {
            return TimeUnit.DAYS.toMillis(rawLength) * 30;
        }
    },

    YEARS("years", "year", "y") {
        public long convertTime(long rawLength) {
            return TimeUnit.DAYS.toMillis(rawLength) * 365;
        }
    },

    UNDEFINED("undefined") {
        public long convertTime(long rawLength) {
            return -1;
        }
    };


    private final String name;
    private final String[] aliases;

    Unit(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }
    
    /**
     * Converts the privided time into milliseconds
     * @param rawLength The length in the context of the unit
     * @return The full time in milliseconds
     */
    public abstract long convertTime(long rawLength);
    
    /**
     * Matches a string to the unit type
     * @param unitString The raw string
     * @return The matched unit or undefined if it could not be found. Should never be null.
     */
    public static Unit matchUnit(String unitString) {
        for (Unit unit : values()) {
            if (unit.getName().equalsIgnoreCase(unitString)) {
                return unit;
            }

            for (String alias : unit.getAliases()) {
                if (alias.equalsIgnoreCase(unitString)) {
                    return unit;
                }
            }
        }

        return UNDEFINED;
    }
}