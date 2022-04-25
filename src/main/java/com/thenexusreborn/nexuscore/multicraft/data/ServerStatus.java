package com.thenexusreborn.nexuscore.multicraft.data;

import java.util.*;

public class ServerStatus implements MulticraftObject {
    public int serverId, onlinePlayers, maxPlayers;
    public String status;
    public Set<PlayerStatus> players = new HashSet<>();
}
