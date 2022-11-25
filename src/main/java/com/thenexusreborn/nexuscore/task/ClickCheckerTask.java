package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusTask;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;

import java.util.HashSet;

public class ClickCheckerTask extends NexusTask<NexusCore> {

    public ClickCheckerTask(NexusCore plugin) {
        super(plugin, 20L, 0L, false);
    }

    public void onRun() {
        for (NexusPlayer player : new HashSet<>(NexusAPI.getApi().getPlayerManager().getPlayers().values())) {
            if (!player.isOnline()) {
                continue;
            }

            if (player.getCPS() > 16) {
                player.sendMessage(MCUtils.color(MsgType.WARN + "You are clicking at " + player.getCPS() + " and the server limit is 16. If you do this too long, you may get auto-banned by the anti-cheat."));
            }

            player.resetCPS();
        }
    }
}