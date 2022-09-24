package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLoadActionBarTask extends BukkitRunnable {

    private NexusCore plugin;

    public PlayerLoadActionBarTask(NexusCore plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer == null) {
                SpigotUtils.sendActionBar(player, "&cPlease wait while your data is being loaded");
            }
        }
    }

    public void start() {
        runTaskTimer(plugin, 0L, 20L);
    }
}
