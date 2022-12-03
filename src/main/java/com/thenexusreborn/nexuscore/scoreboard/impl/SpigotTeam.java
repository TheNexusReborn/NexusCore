package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.scoreboard.ValueUpdater;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class SpigotTeam implements ITeam {
    
    private final Team team;
    private ValueUpdater valueUpdater;
    
    public SpigotTeam(Team team) {
        this.team = team;
    }
    
    @Override
    public void addEntry(String text) {
        team.addEntry(text);
    }
    
    @Override
    public Set<String> getEntries() {
        return team.getEntries();
    }
    
    @Override
    public void unregister() {
        team.unregister();
    }
    
    @Override
    public String getName() {
        return team.getName();
    }
    
    @Override
    public void setPrefix(String prefix) {
        team.setPrefix(MCUtils.color(prefix));
    }
    
    @Override
    public void setSuffix(String suffix) {
        team.setSuffix(MCUtils.color(suffix));
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
