package com.thenexusreborn.api.server;

public abstract non-sealed class ProxyServer extends NexusServer {
    public ProxyServer(String name) {
        super(name, ServerType.PROXY, "proxy", 100);
    }
}