package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerHUDThread extends StarThread<NexusCore> {

    public PlayerHUDThread(NexusCore plugin) {
        super(plugin, 1L, 0L, false);
    }

    @Override
    public void onRun() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());

            if (nexusPlayer == null) {
                continue;
            }

            if (nexusPlayer.getScoreboard() != null) {
                nexusPlayer.getScoreboard().update();
            }
            
            IActionBar actionBar = nexusPlayer.getActionBar();
            if (actionBar != null) {
                String text = actionBar.getText();
                if (text != null) {
                    SpigotUtils.sendActionBar(player, text);
                }
            }
        }
    }
}
