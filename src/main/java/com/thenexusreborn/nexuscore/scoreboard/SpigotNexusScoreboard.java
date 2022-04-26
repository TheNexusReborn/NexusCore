package com.thenexusreborn.nexuscore.scoreboard;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.nexuscore.scoreboard.impl.SpigotScoreboard;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.Bukkit;

public class SpigotNexusScoreboard extends NexusScoreboard {
    public SpigotNexusScoreboard(NexusPlayer player) {
        super(player);
    }
    
    @Override
    public void init() {
        this.scoreboard = new SpigotScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        createPlayerTeam(player);
    }
    
    @Override
    public void setTeamDisplayOptions(NexusPlayer nexusPlayer, ITeam team) {
        if (nexusPlayer.getRank() == Rank.MEMBER) {
            team.setPrefix(MCUtils.color(nexusPlayer.getRank().getColor()));
        } else {
            team.setPrefix(MCUtils.color(nexusPlayer.getRank().getPrefix() + " &r"));
        }
        if (nexusPlayer.getTag() != null) {
            team.setSuffix(MCUtils.color(" " + nexusPlayer.getTag().getDisplayName()));
        } else {
            team.setSuffix("");
        }
    }
}
