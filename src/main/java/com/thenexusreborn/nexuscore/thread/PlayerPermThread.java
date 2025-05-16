package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public class PlayerPermThread extends StarThread<NexusCore> {
    
    public PlayerPermThread(NexusCore plugin) {
        super(plugin, 20L, 0L, true);
    }
    
    public void onRun() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer != null) {
                if (nexusPlayer.getRank().ordinal() > Rank.HELPER.ordinal()) {
                    Set<PermissionAttachmentInfo> effectivePermissions = player.getEffectivePermissions();
                    for (PermissionAttachmentInfo perm : effectivePermissions) {
                        if (perm.getPermission().equals("vulcan.alerts")) {
                            player.removeAttachment(perm.getAttachment());
                        }
                    }
                }
            }
        }
    }
}
