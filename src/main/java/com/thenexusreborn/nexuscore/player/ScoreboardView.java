package com.thenexusreborn.nexuscore.player;

import org.bukkit.scoreboard.*;

import java.util.List;

public abstract class ScoreboardView {
    
    protected NexusScoreboard scoreboard;
    protected Objective objective;
    
    public ScoreboardView(NexusScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public abstract void registerTeams(Scoreboard scoreboard, Objective objective);
    public abstract void update();
    public abstract Objective registerObjective(Scoreboard scoreboard);
    public abstract List<String> getTeams();
    
    protected void addEntry(Objective objective, Team team, String text, int score) {
        team.addEntry(text);
        objective.getScore(text).setScore(score);
    }
    
    public void setObjective(Objective objective) {
        this.objective = objective;
    }
    
    public Objective getObjective() {
        return objective;
    }
}
