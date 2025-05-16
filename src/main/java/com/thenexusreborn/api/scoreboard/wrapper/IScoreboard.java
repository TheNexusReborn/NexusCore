package com.thenexusreborn.api.scoreboard.wrapper;

import java.util.Set;

public interface IScoreboard {
    ITeam getTeam(String team);
    
    void resetScores(String entry);
    
    ITeam registerNewTeam(String name);
    
    Set<ITeam> getTeams();
    
    IObjective registerNewObjective(String name);
}
