package com.thenexusreborn.nexuscore.util;

/**
 * Class for global constants that would either be annoying to always declare or just used a lot
 */
public class Constants {
    
    /**
     * A date format to make things consistent. This must be converted into a SimpleDateFormat when used
     */
    public static final String DATE_FORMAT = "MM/dd/yyyy h:mm:ss a z";
    
    /**
     * A non-currency based decimal format with commas. This accounts for the maximum long value
     */
    public static final String NUMBER_FORMAT = "#,###,###,###,###,###,###.#";
}