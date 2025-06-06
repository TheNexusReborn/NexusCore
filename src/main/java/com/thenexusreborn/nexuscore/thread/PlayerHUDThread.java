package com.thenexusreborn.nexuscore.thread;

import com.stardevllc.starcore.utils.StarThread;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerHUDThread extends StarThread<NexusCore> {

    public PlayerHUDThread(NexusCore plugin) {
        super(plugin, 1L, 10L, false);
    }

    @Override
    public void onRun() {
        for (UUID pUUID : plugin.getNexusServer().getPlayers()) {
            Player player = Bukkit.getPlayer(pUUID);
            if (pUUID == null) {
                continue;
            }
            
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
            
            if (nexusPlayer == null) {
                SpigotUtils.sendActionBar(player, "&cPlease wait while your data is being loaded");
                continue;
            }
            
            if (nexusPlayer.getScoreboard() != null) {
                nexusPlayer.getScoreboard().update();
                TablistHandler tablistHandler = nexusPlayer.getScoreboard().getTablistHandler();
                if (tablistHandler != null) {
                    tablistHandler.update();
                }
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