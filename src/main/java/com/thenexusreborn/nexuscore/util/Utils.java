package com.thenexusreborn.nexuscore.util;

import java.lang.reflect.Constructor;
import java.util.*;

public final class Utils {
    
    public static String mysqlDriverClass, sqliteDriverClass;
    
    private static final String alphabet = "abcdefghigklmnopqrstuvwzyz", numbers = "0123456789";
    
    static {
        try {
            Class<?> sqlDriver = Class.forName("com.mysql.cj.jdbc.Driver");
            Constructor<?> constructor = sqlDriver.getDeclaredConstructor();
            Object o = constructor.newInstance();
            mysqlDriverClass = o.getClass().getName();
        } catch (Exception e) {
            System.out.println("Error while loading the mysql driver class");
        }
        
        try {
            Class<?> sqlDriver = Class.forName("org.sqlite.JDBC");
            Constructor<?> constructor = sqlDriver.getDeclaredConstructor();
            Object o = constructor.newInstance();
            sqliteDriverClass = o.getClass().getName();
        } catch (Exception e) {
            System.out.println("Problem with loading the sqlite driver class");
        }
    }
    
    /**
     * Generates a random string of characters
     * @param amount The amount of characters
     * @param caps To use capital letters, this is randomized and is not guaranteed
     * @param useAlphabet Use alphabet characters a-z of the US Alphabet, either useAlphabet or useNumbers needs to be true, both cannot be false
     * @param useNumbers Use number characters 0-9, either useAlphabet or useNumbers needs to be true, both cannot be false
     * @return The generated string
     */
    public static String generateCode(int amount, boolean caps, boolean useAlphabet, boolean useNumbers) {
        if (!useAlphabet && !useNumbers) {
            throw new IllegalArgumentException("Invalid parameters, you must specify to use the alphabet or to use numbers, both cannot be false");
        }
        
        List<String> chars = new LinkedList<>();
        if (useAlphabet) {
            chars.addAll(Arrays.asList(alphabet.split("")));
        } 
        if (useNumbers) {
            chars.addAll(Arrays.asList(numbers.split("")));
        }
                
        List<String> allowedCharacters = Collections.unmodifiableList(chars);
    
        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < amount; i++) {
            String c = allowedCharacters.get(random.nextInt(allowedCharacters.size()));
            if (caps) {
                if (random.nextInt(100) < 50) {
                    c = c.toUpperCase();
                }
            }
            codeBuilder.append(c);
        }
        
        return codeBuilder.toString();
    }
    
    /**
     * This is the fully qualified name of the current mysql driver that respects JDBC 
     * @return The class name of the mysql driver
     */
    public static String getMysqlDriverClass() {
        return mysqlDriverClass;
    }
    
    /**
     * This is the fully qualified name of the current sqlite driver that respects JDBC 
     * @return The class name of the sqlite driver
     */
    public static String getSqliteDriverClass() {
        return sqliteDriverClass;
    }
    
    /**
     * Prints the current stack track to System.out 
     */
    public static void printCurrentStack() {
        System.out.println(Arrays.toString(new Throwable().getStackTrace()));
    }
}