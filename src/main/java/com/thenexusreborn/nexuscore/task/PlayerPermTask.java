package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public class PlayerPermTask extends NexusTask<NexusCore> {
    
    public PlayerPermTask(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
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
}
