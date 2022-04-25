package com.thenexusreborn.nexuscore.util.collection;

import java.util.*;

/**
 * A class that provides some nicer methods to interact with a HashMap in a HashMap
 * @param <K> The main key or First Key
 * @param <S> The key of the inner hashmap or the Second Key
 * @param <V> The value in the second hashmap
 */
public class MultiMap<K, S, V> extends HashMap<K, Map<S, V>> {
    
    /**
     * Adds a value to the inner hashmap of the first and second keys
     * @param firstKey The first key
     * @param secondKey The second key
     * @param value The value
     */
    public void put(K firstKey, S secondKey, V value) {
        Map<S, V> map = getOrDefault(firstKey, new HashMap<>());
        map.put(secondKey, value);
        put(firstKey, map);
    }
    
    /**
     * Gets a value
     * @param firstKey The first key
     * @param secondKey The second key
     * @return The value
     */
    public V get(K firstKey, S secondKey) {
        Map<S, V> map = getOrDefault(firstKey, new HashMap<>());
        return map.getOrDefault(secondKey, null);
    }
}
