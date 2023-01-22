package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.scoreboard.wrapper.*;
import org.bukkit.scoreboard.*;

import java.util.*;

public record SpigotScoreboard(Scoreboard scoreboard) implements IScoreboard {
    
    @Override
    public ITeam getTeam(String team) {
        Team t = scoreboard.getTeam(team);
        if (t == null) {
            return null;
        }
        return new SpigotTeam(t);
    }
    
    @Override
    public boolean isRegistered(ITeam team) {
        return scoreboard.getTeam(team.getName()) != null;
    }
    
    @Override
    public void resetScores(String entry) {
        scoreboard.resetScores(entry);
    }
    
    @Override
    public ITeam registerNewTeam(String name) {
        return new SpigotTeam(scoreboard.registerNewTeam(name));
    }
    
    @Override
    public void registerTeam(ITeam team) {
        scoreboard.registerNewTeam(team.getName());
    }
    
    @Override
    public Set<ITeam> getTeams() {
        Set<ITeam> teams = new HashSet<>();
        for (Team team : this.scoreboard.getTeams()) {
            teams.add(new SpigotTeam(team));
        }
        return teams;
    }
    
    @Override
    public IObjective registerNewObjective(String name) {
        return new SpigotObjective(this.scoreboard.registerNewObjective(name, "dummy"));
    }
}
