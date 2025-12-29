package com.thenexusreborn.api.server;

import com.stardevllc.starlib.objects.registry.Registry;

public class ServerRegistry<T extends NexusServer> extends Registry<String, T> {
    public ServerRegistry() {
        super(null, string -> string.toLowerCase().replace(" ", "_"), NexusServer::getName, null, null);
    }
}