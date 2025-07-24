package com.thenexusreborn.api.registry;

import com.stardevllc.starlib.registry.StringRegistry;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.player.Toggle.Info;

public class ToggleRegistry extends StringRegistry<Info> {
    public void register(String name, Rank rank, String displayName, String description, boolean defaultValue) {
        register(name, new Info(name, rank, displayName, description, defaultValue));
    }
    
    @Override
    public Info get(String str) {
        for (Info object : this.getObjects().values()) {
            if (object.getName().equalsIgnoreCase(str)) {
                return object;
            }
        }
        return null;
    }
}
