package com.thenexusreborn.api.server;

import com.stardevllc.registry.StringRegistry;

public class ServerRegistry<T extends NexusServer> extends StringRegistry<T> {
    public ServerRegistry() {
        super(null, string -> string.toLowerCase().replace(" ", "_"), NexusServer::getName, null, null);
    }
}