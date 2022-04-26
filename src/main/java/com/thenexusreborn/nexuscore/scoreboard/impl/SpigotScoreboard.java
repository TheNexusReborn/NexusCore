package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.scoreboard.wrapper.*;
import org.bukkit.scoreboard.*;

import java.util.*;

public class SpigotScoreboard implements IScoreboard {
    
    private Scoreboard scoreboard;
    
    public SpigotScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    @Override
    public ITeam getTeam(String team) {
        return new SpigotTeam(scoreboard.getTeam(team));
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
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}
