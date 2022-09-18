package com.thenexusreborn.nexuscore.util.timer;

import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerRunnable extends BukkitRunnable {
    private final Timer timer;
    
    public TimerRunnable(Timer timer) {
        this.timer = timer;
    }
    
    @Override
    public void run() {
        if (timer.isCancelled()) {
            cancel();
            return;
        }
    
        if (timer.isPaused()) {
            return;
        }
    
        if (timer.getTimeLeft() <= 0) {
            return;
        }
    
        timer.count();
    
        Bukkit.getScheduler().runTask(NexusCore.getPlugin(NexusCore.class), () -> {
            TimerSnapshot timerSnapshot = new TimerSnapshot(timer, timer.getTimeLeft());
            Boolean value = timer.getCallback().callback(timerSnapshot);
            if (!value) {
                cancel();
            }
        });
    }
}
