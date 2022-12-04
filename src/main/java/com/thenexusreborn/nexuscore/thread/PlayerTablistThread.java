package com.thenexusreborn.nexuscore.thread;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerTablistThread extends NexusThread<NexusCore> {
    
    public PlayerTablistThread(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer != null) {
                if (nexusPlayer.getScoreboard() != null) {
                    TablistHandler tablistHandler = nexusPlayer.getScoreboard().getTablistHandler();
                    if (tablistHandler != null) {
                        tablistHandler.update();
                    }
                }
            }
        }
    }
}