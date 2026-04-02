package com.thenexusreborn.api.registry;

import com.stardevllc.starlib.objects.key.Keys;
import com.stardevllc.starlib.registry.HashRegistry;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.player.Toggle.Info;

public class ToggleRegistry extends HashRegistry<Info> {
    public ToggleRegistry() {
        super(Info.class, Keys.of("nexuscore:toggles"), "Toggles", null, false, null, null);
    }
    
    public void register(String name, Rank rank, String displayName, String description, boolean defaultValue) {
        register(name, new Info(name, rank, displayName, description, defaultValue));
    }
}
