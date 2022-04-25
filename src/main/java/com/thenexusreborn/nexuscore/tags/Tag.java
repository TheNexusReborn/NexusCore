package com.thenexusreborn.nexuscore.tags;

public class Tag {
    private final String name;
    
    public Tag(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return "&d&l" + this.name.toUpperCase();
    }
}
