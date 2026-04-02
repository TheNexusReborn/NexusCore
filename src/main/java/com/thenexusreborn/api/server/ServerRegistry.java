package com.thenexusreborn.api.server;

import com.stardevllc.starlib.objects.key.Keys;
import com.stardevllc.starlib.registry.HashRegistry;

public class ServerRegistry<T extends NexusServer> extends HashRegistry<T> {
    public ServerRegistry(Class<T> valueType) {
        super(valueType, Keys.of("nexuscore:servers"), "Servers", null, false, null, null);
    }
}