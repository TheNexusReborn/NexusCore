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
        try {
            objective.unregister();
        } catch (Exception e) {
        
        }
    }
    
    @Override
    public void setDisplayName(String displayName) {
        try {
            this.objective.setDisplayName(displayName);
        } catch (Exception e) {
            
        }
    }
}
