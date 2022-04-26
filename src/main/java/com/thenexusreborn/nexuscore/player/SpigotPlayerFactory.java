package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;

import java.util.*;

public class SpigotPlayerFactory extends PlayerFactory {
    @Override
    public NexusPlayer createPlayer(UUID uuid, Map<Rank, Long> ranks, long firstJoined, long lastLogin, long lastLogout, long playtime, String lastKnownName, Tag tag) {
        return new SpigotNexusPlayer(uuid, ranks, firstJoined, lastLogin, lastLogout, playtime, lastKnownName, tag);
    }
    
    @Override
    public NexusPlayer createPlayer(UUID uniqueId, String name) {
        return new SpigotNexusPlayer(uniqueId, name);
    }
}
