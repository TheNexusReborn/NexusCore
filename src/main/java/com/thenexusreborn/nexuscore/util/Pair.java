package com.thenexusreborn.nexuscore.util;

/**
 * A class representing a pair of objects
 */
public class Pair<K, V> {
    
    private final K firstValue;
    private final V secondValue;
    
    public Pair(K firstValue, V secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }
    
    /**
     * @return First object
     */
    public K getFirstValue() {
        return firstValue;
    }

    /**
     * @return Second object
     */
    public V getSecondValue() {
        return secondValue;
    }
}