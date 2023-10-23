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
    public ITeam createTeam(TeamBuilder teamBuilder) {
        ITeam team = this.scoreboard.getScoreboard().registerNewTeam(teamBuilder.getName());
        if (teamBuilder.getPrefix() != null) {
            team.setPrefix(teamBuilder.getPrefix());
        }

        if (teamBuilder.getSuffix() != null) {
            team.setSuffix(teamBuilder.getSuffix());
        }

        if (teamBuilder.getValueUpdater() != null) {
            team.setValueUpdater(teamBuilder.getValueUpdater());
        }

        addEntry(objective, team, MCUtils.color(teamBuilder.getEntry()), teamBuilder.getScore());
        this.teams.add(team);
        return team;
    }

    @Override
    public void registerObjective() {
        this.objective = scoreboard.getScoreboard().registerNewObjective(name);
        this.objective.setDisplayName(displayName);
        ((SpigotObjective) objective).objective().setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void unregisterTeams() {
        for (ITeam team : this.getTeams()) {
            for (String entry : team.getEntries()) {
                this.scoreboard.getScoreboard().resetScores(entry);
            }
            team.unregister();
        }
    }
}
