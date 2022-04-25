package com.thenexusreborn.nexuscore.util.collection;

import java.util.*;

/**
 * Map that helps to more easily manage a collection as the value
 * This is useful for a few things
 * @param <K> The key for the map
 * @param <V> The value for the List in the Map
 */
public class ListMap<K, V> extends HashMap<K, List<V>> {
    /**
     * Adds a value based on a keu
     * @param key The key 
     * @param value The value
     */
    public void add(K key, V value) {
        if (get(key) != null) {
            get(key).add(value);
        } else {
            put(key, new ArrayList<>(Collections.singletonList(value)));
        }
    }
}