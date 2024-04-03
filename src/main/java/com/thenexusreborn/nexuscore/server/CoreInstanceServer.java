package com.thenexusreborn.nexuscore.server;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.server.InstanceServer;
import com.thenexusreborn.nexuscore.NexusCore;

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
            primaryVirtualServer.join(nexusPlayer);
            name = primaryVirtualServer.getName();
        }
        
        if (nexusPlayer.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
            plugin.getStaffChannel().sendMessage(nexusPlayer.getDisplayName() + " &7&l-> &6" + name);
        }
    }

    @Override
    public void quit(NexusPlayer nexusPlayer) {
        getChildServers().forEach(s -> s.quit(nexusPlayer));
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
