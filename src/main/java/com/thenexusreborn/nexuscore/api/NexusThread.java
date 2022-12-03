package com.thenexusreborn.nexuscore.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class NexusThread<T extends JavaPlugin> extends BukkitRunnable {
    
    private static final List<NexusThread<?>> NEXUS_THREADS = new ArrayList<>();
    
    protected T plugin;
    
    protected ThreadOptions threadOptions;
    
    //This is the performance metrics
    private long minTime, maxTime;
    private long totalRuns;
    private long[] mostRecent = new long[100];
    private int mostRecentCounter;
    
    public NexusThread(T plugin, long period, long delay, boolean async) {
        this(plugin, new ThreadOptions().period(period).delay(delay).async(async).repeating(true));
    }
    
    public NexusThread(T plugin, long period, boolean async) {
        this(plugin, period, 0L, async);
    }
    
    public NexusThread(T plugin, ThreadOptions threadOptions) {
        this.plugin = plugin;
        this.threadOptions = threadOptions;
        NEXUS_THREADS.add(this);
    }
    
    public final void run() {
        long start = System.currentTimeMillis();
        this.onRun();
        long end = System.currentTimeMillis();
        
        this.totalRuns++;
        
        long runTime = end - start;
        
        if (this.minTime == 0 || runTime < this.minTime) {
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
    
    public abstract void onRun();
    
    public NexusThread<T> start() {
        if (!this.threadOptions.isRepeating()) {
            if (this.threadOptions.isAsync()) {
                runTaskAsynchronously(plugin);
            } else {
                runTask(plugin);
            }
        } else {
            if (this.threadOptions.isAsync()) {
                runTaskTimerAsynchronously(plugin, threadOptions.getDelay(), threadOptions.getPeriod());
            } else {
                runTaskTimer(plugin, threadOptions.getDelay(), threadOptions.getPeriod());
            }
        }
        return this;
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
        
        if (total == 0) {
            return 0;
        }
        
        return totalTime / total;
    }
    
    public ThreadOptions getThreadOptions() {
        return threadOptions;
    }
    
    public boolean isAsync() {
        return threadOptions.isAsync();
    }
    
    public static List<NexusThread<?>> getNexusThreads() {
        return new ArrayList<>(NEXUS_THREADS);
    }
    
    public long getPeriod() {
        return this.threadOptions.getPeriod();
    }
    
    public static class ThreadOptions {
        private boolean async, repeating;
        private long period, delay;
        
        public ThreadOptions async(boolean async) {
            this.async = async;
            return this;
        }
        
        public ThreadOptions repeating(boolean repeating) {
            this.repeating = repeating;
            return this;
        }
        
        public ThreadOptions period(long period) {
            this.period = period;
            return this;
        }
        
        public ThreadOptions delay(long delay) {
            this.delay = delay;
            return this;
        }
        
        public boolean isAsync() {
            return async;
        }
        
        public boolean isRepeating() {
            return repeating;
        }
        
        public long getPeriod() {
            return period;
        }
        
        public long getDelay() {
            return delay;
        }
    }
}
