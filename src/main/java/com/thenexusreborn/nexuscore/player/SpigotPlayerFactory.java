package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.*;

public class SpigotPlayerFactory extends PlayerFactory {
    @Override
    public PlayerProxy createProxy(NexusPlayer player) {
        return new SpigotPlayerProxy(player.getUniqueId());
    }
}
