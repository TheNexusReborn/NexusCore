package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTablistTask extends BukkitRunnable {
    
    private JavaPlugin plugin;
    
    public PlayerTablistTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
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
    
    public void start() {
        runTaskTimerAsynchronously(plugin, 1L, 20L);
    }
}
