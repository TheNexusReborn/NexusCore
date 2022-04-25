package com.thenexusreborn.nexuscore.util.helper;

import java.util.*;

/**
 * A collection of helper methods for strings
 */
public final class StringHelper {
    /**
     * This capitalizes every word in a string. The spaces should be underscores.
     * This is mainly useful with Enums
     * @param string The string to format
     * @return The formatted string
     */
    public static String capitalizeEveryWord(String string) {
        string = string.toLowerCase();
        String[] words = string.split("_");
        StringBuilder name = new StringBuilder();
        for (int w = 0; w < words.length; w++) {
            String word = words[w];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                if (i == 0) {
                    sb.append(Character.toUpperCase(word.charAt(i)));
                } else {
                    sb.append(word.charAt(i));
                }
            }
            name.append(sb);
            if (w < (words.length - 1)) {
                name.append(" ");
            }
        }
        
        return name.toString();
    }
    
    /**
     * Joins a collection with a separator. This is useful if Apache is not in the dependency list
     * @param collection The collection to join
     * @param separator The separator
     * @return The joined collection
     */
    public static String join(Collection<?> collection, String separator) {
        Iterator<?> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (first == null)
                return "";
            if (!iterator.hasNext()) {
                return first.toString();
            } else {
                StringBuilder buf = new StringBuilder();
                buf.append(first);
    
                while (iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }
    
    /**
     * Checks to see if the string is empty. This also takes care of the null check as well
     * @param str The string to check
     * @return If the string is empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
