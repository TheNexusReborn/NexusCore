package com.thenexusreborn.nexuscore.util.collection;

import java.util.TreeMap;

/**
 * Custom collection for maps who's integer keys auto-increment.
 * @param <T> The value for the map
 */
public class IncrementalMap<T> extends TreeMap<Integer, T> {
    /**
     * Adds something to this map
     * @param value The value to add
     * @return The key of the added value
     */
    public int add(T value) {
        int index = 0;
        if (!isEmpty()) {
            index = lastKey() + 1;
        }
        
        put(index, value);
        return index;
    }
}