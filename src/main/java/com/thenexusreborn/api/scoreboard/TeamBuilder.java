package com.thenexusreborn.api.scoreboard;

public class TeamBuilder {
    private String name, prefix, suffix, entry;
    private int score;
    private ValueUpdater valueUpdater;
    
    public TeamBuilder(String name) {
        this.name = name;
    }
    
    public TeamBuilder valueUpdater(ValueUpdater valueUpdater) {
        this.valueUpdater = valueUpdater;
        return this;
    }
    
    public TeamBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public TeamBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
    
    public TeamBuilder suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }
    
    public TeamBuilder entry(String entry) {
        this.entry = entry;
        return this;
    }
    
    public TeamBuilder entry(Object obj) {
        return this.entry(obj.toString());
    }
    
    public TeamBuilder score(int score) {
        this.score = score;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public String getEntry() {
        return entry;
    }
    
    public int getScore() {
        return score;
    }
    
    public ValueUpdater getValueUpdater() {
        return valueUpdater;
    }
}
