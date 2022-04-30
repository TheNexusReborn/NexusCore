package com.thenexusreborn.nexuscore.scoreboard;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.nexuscore.scoreboard.impl.SpigotScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpigotNexusScoreboard extends NexusScoreboard {
    public SpigotNexusScoreboard(NexusPlayer player) {
        super(player);
    }
    
    @Override
    public void init() {
        this.scoreboard = new SpigotScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
    
    @Override
    public void apply() {
        Player bukkitPlayer = Bukkit.getPlayer(this.player.getUniqueId());
        if (bukkitPlayer != null) {
            bukkitPlayer.setScoreboard(((SpigotScoreboard) this.scoreboard).getScoreboard());
        }
    }
}
