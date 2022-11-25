package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerLoadActionBarTask extends NexusTask<NexusCore> {

    public PlayerLoadActionBarTask(NexusCore plugin) {
        super(plugin, 20L, 0L, false);
    }

    public void onRun() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer == null) {
                SpigotUtils.sendActionBar(player, "&cPlease wait while your data is being loaded");
            }
        }
    }
}
