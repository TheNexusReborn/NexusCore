package com.thenexusreborn.nexuscore.util.helper;

import com.thenexusreborn.nexuscore.util.Constants;

import java.text.DecimalFormat;
import java.util.*;

/**
 * A collection of helper methods for numbers
 */
public final class NumberHelper {
    
    private static final LinkedHashMap<String, Integer> romanNumerals = new LinkedHashMap<>();
    
    static {
        romanNumerals.put("M", 1000);
        romanNumerals.put("CM", 900);
        romanNumerals.put("D", 500);
        romanNumerals.put("CD", 400);
        romanNumerals.put("C", 100);
        romanNumerals.put("XC", 90);
        romanNumerals.put("L", 50);
        romanNumerals.put("XL", 40);
        romanNumerals.put("X", 10);
        romanNumerals.put("IX", 9);
        romanNumerals.put("V", 5);
        romanNumerals.put("IV", 4);
        romanNumerals.put("I", 1);
    }
    
    /**
     * Converts a number into the roman numeral equivilent
     *
     * @param number The number to convert
     * @return The roman numeral representation
     */
    public static String romanNumerals(int number) {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, Integer> entry : romanNumerals.entrySet()) {
            int matches = number / entry.getValue();
            for (int i = 0; i < matches; i++) {
                res.append(entry.getKey());
            }
            number = number % entry.getValue();
        }
        return res.toString();
    }
    
    /**
     * Formats a number using Constants.NUMBER_FORMAT. This is a simple wrapper method
     * @param number The number for format
     * @return The formatted number from DecimalFormat
     */
    public static String formatNumber(Number number) {
        return new DecimalFormat(Constants.NUMBER_FORMAT).format(number);
    }
}
