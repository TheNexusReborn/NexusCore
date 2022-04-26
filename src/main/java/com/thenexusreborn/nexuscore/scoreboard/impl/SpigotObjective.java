package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.scoreboard.wrapper.*;
import org.bukkit.scoreboard.Objective;

public class SpigotObjective implements IObjective {
    
    private Objective objective;
    
    public SpigotObjective(Objective objective) {
        this.objective = objective;
    }
    
    public Objective getObjective() {
        return objective;
    }
    
    @Override
    public IScore getScore(String text) {
        return new SpigotScore(objective.getScore(text));
    }
    
    @Override
    public void unregister() {
        objective.unregister();
    }
    
    @Override
    public void setDisplayName(String displayName) {
        this.objective.setDisplayName(displayName);
    }
}
