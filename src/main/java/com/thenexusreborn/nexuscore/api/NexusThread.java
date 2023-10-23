package com.thenexusreborn.nexuscore.api;

import me.firestar311.starlib.spigot.utils.StarThread;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class NexusThread<T extends JavaPlugin> extends StarThread<T> {
    public NexusThread(T plugin, long period, long delay, boolean async) {
        super(plugin, period, delay, async);
    }

    public NexusThread(T plugin, long period, boolean async) {
        super(plugin, period, async);
    }

    public NexusThread(T plugin, ThreadOptions threadOptions) {
        super(plugin, threadOptions);
    }
}
