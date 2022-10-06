package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class PlayerPermTask extends BukkitRunnable {
    
    private JavaPlugin plugin;
    
    public PlayerPermTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer != null) {
                if (nexusPlayer.getRanks().get().ordinal() > Rank.HELPER.ordinal()) {
                    Set<PermissionAttachmentInfo> effectivePermissions = player.getEffectivePermissions();
                    for (PermissionAttachmentInfo perm : effectivePermissions) {
                        if (perm.getPermission().equalsIgnoreCase("spartan.info") || perm.getPermission().equals("spartan.notifications")) {
                            player.removeAttachment(perm.getAttachment());
                        }
                    }
                }
            }
        }
    }
    
    public void start() {
        runTaskTimerAsynchronously(plugin, 1L, 20L);
    }
}
