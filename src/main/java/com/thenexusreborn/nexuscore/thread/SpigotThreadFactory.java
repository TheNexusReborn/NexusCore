package com.thenexusreborn.nexuscore.thread;

import com.thenexusreborn.api.thread.ThreadFactory;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.Bukkit;

public class SpigotThreadFactory extends ThreadFactory {
    
    private NexusCore plugin;
    
    public SpigotThreadFactory(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }
    
    @Override
    public void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
