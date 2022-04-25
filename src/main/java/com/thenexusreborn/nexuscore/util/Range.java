package com.thenexusreborn.nexuscore.util;

import java.util.Objects;

/**
 * A utility to represent an object based on a range of numbers
 * @param <T> The object type that is represented
 */
public class Range<T> {
    
    private final int min;
    private final int max;
    private final T object;
    
    public Range(int min, int max, T object) {
        this.min = min;
        this.max = max;
        this.object = object;
    }
    
    /**
     * Tests to see if a number is within the range
     * @param number The number to test
     * @return If the number is a number represented by this range object
     */
    public boolean contains(int number) {
        return (number >= min && number <= max);
    }
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }
    
    public T getObject() {
        return object;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Range<?> range = (Range<?>) o;
        return min == range.min && max == range.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}