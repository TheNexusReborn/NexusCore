package com.thenexusreborn.nexuscore.scoreboard;

import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.nexuscore.scoreboard.impl.SpigotObjective;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.scoreboard.DisplaySlot;

public abstract class SpigotScoreboardView extends ScoreboardView {
    public SpigotScoreboardView(NexusScoreboard scoreboard, String name, String displayName) {
        super(scoreboard, name, displayName);
    }
    
    @Override
    public void registerObjective() {
        this.objective = scoreboard.getScoreboard().registerNewObjective(name);
        this.objective.setDisplayName(displayName);
        ((SpigotObjective) objective).objective().setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    @Override
    public ITeam createTeam(String teamName, String entry, int score) {
        return createTeam(teamName, entry, null, null, score);
    }
    
    @Override
    public ITeam createTeam(String teamName, String entry, String prefix, int score) {
        return createTeam(teamName, entry, prefix, null, score);
    }
    
    @Override
    public ITeam createTeam(String teamName, String entry, String prefix, String suffix, int score) {
        ITeam team = this.scoreboard.getScoreboard().registerNewTeam(teamName);
        if (prefix != null) {
            team.setPrefix(MCUtils.color(prefix));
        }
        if (suffix != null) {
            team.setSuffix(MCUtils.color(suffix));
        }
        addEntry(objective, team, MCUtils.color(entry), score);
        return team;
    }
}
