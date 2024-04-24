package com.thenexusreborn.nexuscore.api;

import com.stardevllc.starcore.utils.StarThread;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public abstract class NexusThread<T extends JavaPlugin> extends StarThread<T> {
    
    public static final Set<NexusThread<?>> THREADS = new HashSet<>();
    
    public NexusThread(T plugin, long period, long delay, boolean async) {
        super(plugin, period, delay, async);
        THREADS.add(this);
    }

    public NexusThread(T plugin, long period, boolean async) {
        super(plugin, period, async);
        THREADS.add(this);
    }

    public NexusThread(T plugin, ThreadOptions threadOptions) {
        super(plugin, "", threadOptions);
        THREADS.add(this);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        THREADS.remove(this);
        super.cancel();
    }

    public static Set<NexusThread<?>> getThreads() {
        return new HashSet<>(THREADS);
    }
}
