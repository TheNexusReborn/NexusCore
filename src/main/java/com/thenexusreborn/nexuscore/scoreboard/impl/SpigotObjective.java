package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.scoreboard.wrapper.*;
import org.bukkit.scoreboard.Objective;

public record SpigotObjective(Objective objective) implements IObjective {
    
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
