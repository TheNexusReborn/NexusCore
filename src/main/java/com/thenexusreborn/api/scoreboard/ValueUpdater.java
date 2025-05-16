package com.thenexusreborn.api.scoreboard;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;

@FunctionalInterface
public interface ValueUpdater {
    void update(NexusPlayer player, ITeam team);
}
