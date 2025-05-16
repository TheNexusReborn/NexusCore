package com.thenexusreborn.api.scoreboard.wrapper;

public interface IObjective {
    IScore getScore(String text);
    
    void unregister();
    
    void setDisplayName(String displayName);
}
