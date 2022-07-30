package com.thenexusreborn.nexuscore.util.updater;

import com.thenexusreborn.nexuscore.NexusCore;

/**
 * Part of the Updater feature. This is the main class that handles it
 * The Updater sends a Bukkit event based on a certain amount of time based on the UpdateType enum
 * An example is tracking playtime based on this system
 */
public class Updater implements Runnable {
    
    private final NexusCore plugin;

    public Updater(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (UpdateType type : UpdateType.values()) {
            if (type.run()) {
                try {
                    plugin.getServer().getPluginManager().callEvent(new UpdateEvent(type, type.getLastRun(), type.getCurrentRun()));
                } catch (Exception ex) {
                    try {
                        throw new UpdateException(ex);
                    } catch (UpdateException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
        }
    }
}
