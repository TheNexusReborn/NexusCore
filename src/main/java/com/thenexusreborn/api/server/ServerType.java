package com.thenexusreborn.api.server;

public enum ServerType {
    PROXY, //Bungee Proxy, this is handled a bit differently 
    INSTANCE, //This means that the server is a physical instance
    VIRTUAL //This means that it is within an instance server. Used with SG multi-games
}
