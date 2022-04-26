package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class SpigotTeam implements ITeam {
    
    private Team team;
    
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
        team.setPrefix(prefix);
    }
    
    @Override
    public void setSuffix(String suffix) {
        team.setSuffix(suffix);
    }
}
