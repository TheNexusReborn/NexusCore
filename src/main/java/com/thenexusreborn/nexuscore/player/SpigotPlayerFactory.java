package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;

import java.util.*;

public class SpigotPlayerFactory extends PlayerFactory {
    @Override
    public NexusPlayer createPlayer(UUID uuid, Map<Rank, Long> ranks, long firstJoined, long lastLogin, long lastLogout, String lastKnownName, Tag tag, Set<String> unlockedTags, boolean prealpha, boolean alpha, boolean beta) {
        return new SpigotNexusPlayer(uuid, ranks, firstJoined, lastLogin, lastLogout, lastKnownName, tag, unlockedTags, prealpha, alpha, beta);
    }
    
    @Override
    public NexusPlayer createPlayer(UUID uniqueId, String name) {
        return new SpigotNexusPlayer(uniqueId, name);
    }
}
