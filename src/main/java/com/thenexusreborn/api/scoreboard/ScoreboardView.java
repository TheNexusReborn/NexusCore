package com.thenexusreborn.api.scoreboard;

import com.thenexusreborn.api.scoreboard.wrapper.*;

import java.util.*;

public abstract class ScoreboardView {
    
    protected final NexusScoreboard scoreboard;
    protected final String name;
    protected final String displayName;
    protected final Set<ITeam> teams = new HashSet<>();
    protected IObjective objective;
    
    public ScoreboardView(NexusScoreboard scoreboard, String name, String displayName) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.displayName = displayName;
    }
    
    public abstract void registerTeams();
    
    public void update() {
        for (ITeam team : getTeams()) {
            if (team.getValueUpdater() != null) {
                team.getValueUpdater().update(scoreboard.getPlayer(), team);
            }
        }
    }
    public abstract void registerObjective();
    
    protected void addEntry(IObjective objective, ITeam team, String text, int score) {
        team.addEntry(text);
        objective.getScore(text).setScore(score);
    }

    public IObjective getObjective() {
        return objective;
    }
    
    public Set<ITeam> getTeams() {
        return teams;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract ITeam createTeam(TeamBuilder teamBuilder);
    
    public abstract void unregisterTeams();
}
