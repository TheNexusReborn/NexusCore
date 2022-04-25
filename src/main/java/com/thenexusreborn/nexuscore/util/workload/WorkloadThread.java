package com.thenexusreborn.nexuscore.util.workload;

import com.google.common.collect.Queues;
import com.thenexusreborn.nexuscore.util.nms.NMS;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;

public class WorkloadThread extends BukkitRunnable {
    
    private final ArrayDeque<Workload> workloadDeque;
    private NMS nms;
    
    public WorkloadThread(NMS nms) {
        workloadDeque = Queues.newArrayDeque();
        this.nms = nms;
    }
    
    public void addWorkload(Workload workload) {
        this.workloadDeque.add(workload);
    }
    
    @Override
    public void run() {
        double tps = nms.getRecentTPS()[0];
        while (!workloadDeque.isEmpty() && tps > 15) {
            workloadDeque.poll().compute();
        }
    }
    
    public WorkloadThread start(JavaPlugin plugin) {
        this.runTaskTimer(plugin, 0L, 1L);
        return this;
    }
}
