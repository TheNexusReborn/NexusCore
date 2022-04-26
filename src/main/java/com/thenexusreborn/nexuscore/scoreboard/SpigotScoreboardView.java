package com.thenexusreborn.nexuscore.scoreboard;

import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.nexuscore.scoreboard.impl.SpigotObjective;
import org.bukkit.scoreboard.DisplaySlot;

public abstract class SpigotScoreboardView extends ScoreboardView {
    public SpigotScoreboardView(NexusScoreboard scoreboard, String name, String displayName) {
        super(scoreboard, name, displayName);
    }
    
    @Override
    public void registerObjective() {
        this.objective = scoreboard.getScoreboard().registerNewObjective(name);
        this.objective.setDisplayName(displayName);
        ((SpigotObjective) objective).getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
    }
}
