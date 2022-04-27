package com.thenexusreborn.nexuscore.proxy;

import com.google.common.io.*;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ProxyMessageListener implements PluginMessageListener {
    
    private NexusCore plugin;
    
    public ProxyMessageListener(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        boolean isBungeeCord = channel.equals("BungeeCord");
        boolean isNexus = channel.equals("nexus");
    
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        //Do things now
    }
}
