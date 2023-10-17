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
    public void resetScores(String entry) {
        scoreboard.resetScores(entry);
    }
    
    @Override
    public ITeam registerNewTeam(String name) {
        Team team;
        try {
            team = scoreboard.registerNewTeam(name);
        } catch (Exception e) {
            team = scoreboard.getTeam(name);
        }
        return new SpigotTeam(team);
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
        Objective objective;
        try {
            objective = this.scoreboard.registerNewObjective(name, "dummy");
        } catch (Exception e) {
            objective = this.scoreboard.getObjective(name);
        }
        return new SpigotObjective(objective);
    }
}
