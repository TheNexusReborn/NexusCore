package com.thenexusreborn.nexuscore.util.command;

import java.util.*;

public class Argument {
    protected String name;
    protected boolean required;
    protected String errorMessage;
    protected int index;
    protected List<String> completions = new ArrayList<>();
    
    public Argument(String name) {
        this.name = name;
    }
    
    public Argument(String name, boolean required, String errorMessage) {
        this.name = name;
        this.required = required;
        this.errorMessage = errorMessage;
    }
    
    public List<String> getCompletions() {
        return this.completions;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
}
