package com.thenexusreborn.nexuscore.stats;

import com.thenexusreborn.nexuscore.util.Operator;

import java.util.*;

public class StatChange<T> implements Comparable<StatChange<?>> {
    public static final int version = 1;
    
    private int id;
    private final UUID uuid;
    private final String statName;
    private final T value;
    private final Operator operator;
    private final long timestamp;
    
    public StatChange(UUID uuid, String statName, T value, Operator operator, long timestamp) {
        this(-1, uuid, statName, value, operator, timestamp);
    }
    
    public StatChange(int id, UUID uuid, String statName, T value, Operator operator, long timestamp) {
        this.id = id;
        this.uuid = uuid;
        this.statName = statName;
        this.value = value;
        this.operator = operator;
        this.timestamp = timestamp;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getStatName() {
        return statName;
    }
    
    public T getValue() {
        return value;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public int compareTo(StatChange<?> o) {
        return Long.compare(this.getTimestamp(), o.getTimestamp());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatChange<?> that = (StatChange<?>) o;
        return timestamp == that.timestamp && Objects.equals(uuid, that.uuid) && Objects.equals(statName, that.statName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid, statName, timestamp);
    }
}
