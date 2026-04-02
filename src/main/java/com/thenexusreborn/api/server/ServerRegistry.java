package com.thenexusreborn.api.server;

import com.stardevllc.starlib.objects.key.Keys;
import com.stardevllc.starlib.registry.HashRegistry;
import com.stardevllc.starlib.registry.IRegistry;

public class ServerRegistry<T extends NexusServer> extends HashRegistry<T> {
    public ServerRegistry(Class<T> valueType, String key, String name, IRegistry<? super T> parentRegistry) {
        super(valueType, Keys.of(key), name, parentRegistry, false, null, null);
    }
    
    public ServerRegistry(Class<T> valueType, String key, String name) {
        super(valueType, Keys.of(key), name, null, false, null, null);
    }
}