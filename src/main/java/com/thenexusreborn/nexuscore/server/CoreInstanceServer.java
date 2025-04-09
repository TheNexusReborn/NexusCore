package com.thenexusreborn.nexuscore.server;

import com.stardevllc.starchat.context.ChatContext;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.nexuscore.NexusCore;

import java.util.UUID;

public class CoreInstanceServer extends InstanceServer {
    
    private NexusCore plugin;
    
    public CoreInstanceServer(NexusCore plugin) {
        super("Nexus", "undefined", 100);
        this.plugin = plugin;
    }

    @Override
    public void join(NexusPlayer nexusPlayer) {
        String name = getName();
        if (primaryVirtualServer != null) {
            primaryVirtualServer.get().join(nexusPlayer);
            name = primaryVirtualServer.get().getName();
        }
        
        if (nexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
            if (!nexusPlayer.isNicked()) { //TODO Need a change from StarChat to filter receivers
                plugin.getStaffChannel().sendMessage(new ChatContext(nexusPlayer.getTrueDisplayName() + " &7&l-> &6" + name));
            }
        }
    }

    @Override
    public void quit(NexusPlayer nexusPlayer) {
        getChildServers().forEach(s -> s.quit(nexusPlayer));
    }

    @Override
    public void quit(UUID uuid) {
        getChildServers().forEach(s -> s.quit(uuid));
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
