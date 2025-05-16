package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerLoadActionBarThread extends StarThread<NexusCore> {

    public PlayerLoadActionBarThread(NexusCore plugin) {
        super(plugin, 20L, 0L, false);
    }

    public void onRun() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer == null) {
                SpigotUtils.sendActionBar(player, "&cPlease wait while your data is being loaded");
            }
        }
    }
}
