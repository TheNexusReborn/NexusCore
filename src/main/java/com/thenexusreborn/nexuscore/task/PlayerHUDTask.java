package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerHUDTask extends BukkitRunnable {
    
    private JavaPlugin plugin;
    
    public PlayerHUDTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer != null) {
                if (nexusPlayer.getScoreboard() != null) {
                    nexusPlayer.getScoreboard().update();
                }
                IActionBar actionBar = nexusPlayer.getActionBar();
                if (actionBar != null) {
                    SpigotUtils.sendActionBar(player, actionBar.getText());
                }
            }
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 1L, 1L);
    }
}
