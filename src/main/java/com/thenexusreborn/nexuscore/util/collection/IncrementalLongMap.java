package com.thenexusreborn.nexuscore.util.collection;

import java.util.TreeMap;

/**
 * Custom collection for maps who's long keys auto-increment.
 * @param <T> The the value for the map
 */
public class IncrementalLongMap<T> extends TreeMap<Long, T> {
    /**
     * Adds something to this map
     * @param value The value to add
     * @return The key of the added value
     */
    public long add(T value) {
        long index = 0;
        if (!isEmpty()) {
            index = lastKey() + 1;
        }
        
        put(index, value);
        return index;
    }
}