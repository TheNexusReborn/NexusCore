package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerHUDTask extends NexusThread<NexusCore> {
    
    public PlayerHUDTask(NexusCore plugin) {
        super(plugin, 1L, 0L, false);
    }
    
    @Override
    public void onRun() {
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
}
