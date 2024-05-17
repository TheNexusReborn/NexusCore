package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.stardevllc.starcore.color.ColorHandler;
import com.thenexusreborn.api.scoreboard.ValueUpdater;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class SpigotTeam implements ITeam {
    
    private final Team team;
    private ValueUpdater valueUpdater;
    
    public SpigotTeam(Team team) {
        this.team = team;
    }
    
    @Override
    public void addEntry(String text) {
        try {
            team.addEntry(text);
        } catch (Exception e) {
            
        }
    }
    
    @Override
    public Set<String> getEntries() {
        try {
            return team.getEntries();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }
    
    @Override
    public void unregister() {
        try {
            team.unregister();
        } catch (Exception e) {
            
        }
    }
    
    @Override
    public String getName() {
        try {
            return team.getName();
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public void setPrefix(String prefix) {
        try {
            team.setPrefix(ColorHandler.getInstance().color(prefix));
        } catch (Exception e) {}
    }
    
    @Override
    public void setSuffix(String suffix) {
        try {
            team.setSuffix(ColorHandler.getInstance().color(suffix));
        } catch (Exception e) {
            
        }
    }
    
    @Override
    public ValueUpdater getValueUpdater() {
        return valueUpdater;
    }
    
    @Override
    public void setValueUpdater(ValueUpdater valueUpdater) {
        this.valueUpdater = valueUpdater;
    }
}
