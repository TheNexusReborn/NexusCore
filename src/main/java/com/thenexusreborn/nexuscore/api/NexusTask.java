package com.thenexusreborn.nexuscore.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NexusTask<T extends JavaPlugin> extends BukkitRunnable {
    
    protected T plugin;
    
    protected long period;
    protected long delay;
    protected boolean async;
    
    //This is the performance metrics
    private long minTime, maxTime;
    private long totalRuns;
    private long[] mostRecent = new long[100];
    private int mostRecentCounter;
    
    public NexusTask(T plugin, long period, long delay, boolean async) {
        this.plugin = plugin;
        this.period = period;
        this.delay = delay;
        this.async = async;
    }
    
    public void run() {
        long start = System.currentTimeMillis();
        this.onRun();
        long end = System.currentTimeMillis();
    
        this.totalRuns++;
        
        long runTime = end - start;
        
        if (this.minTime != 0 && runTime < this.minTime) {
            this.minTime = runTime;
        }
        
        if (this.maxTime < runTime) {
            this.maxTime = runTime;
        }
        
        if (mostRecentCounter < 99) {
            mostRecent[mostRecentCounter++] = runTime;
        } else {
            long[] mostRecentCopy = new long[100];
            System.arraycopy(this.mostRecent, 1, mostRecentCopy, 0, 99);
            this.mostRecent = mostRecentCopy;
            this.mostRecent[99] = runTime;
        }
    }
    
    //TODO Maybe find a better name?
    public abstract void onRun();
    
    public void start() {
        if (async) {
            runTaskTimerAsynchronously(plugin, delay, period);
        } else {
            runTaskTimer(plugin, delay, period);
        }
    }
    
    public T getPlugin() {
        return plugin;
    }
    
    public long getMinTime() {
        return minTime;
    }
    
    public long getMaxTime() {
        return maxTime;
    }
    
    public long getTotalRuns() {
        return totalRuns;
    }
    
    public long[] getMostRecent() {
        return mostRecent;
    }
    
    public int getMostRecentCounter() {
        return mostRecentCounter;
    }
    
    public long getTotalAverage() {
        return (this.maxTime + this.minTime) / 2;
    }
    
    public long getRecentAverage() {
        long totalTime = 0;
        long total = 0;
        for (long time : this.mostRecent) {
            if (time > 0) {
                totalTime += time;
                total++;
            }
        }
        
        return totalTime / total;
    }
}
