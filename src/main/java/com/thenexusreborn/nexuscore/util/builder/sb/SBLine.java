package com.thenexusreborn.nexuscore.util.builder.sb;

public class SBLine {
    private String prefix, name, suffix;
    
    public SBLine(String prefix, String name, String suffix) {
        this.prefix = prefix;
        this.name = name;
        this.suffix = suffix;
    }
    
    public SBLine(String name) {
        this("", name, "");
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public String toString() {
        return "Prefix: " + this.prefix + ", Name: " + this.name + ", Suffix: " + this.suffix;
    }
}