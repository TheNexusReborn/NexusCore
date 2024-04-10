package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.api.helper.MemoryHelper;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.NexusThread;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PerformanceCmd implements CommandExecutor {

    private final NexusCore plugin;

    public PerformanceCmd(NexusCore plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ColorUtils.color("&6&l>> &d&lThe Nexus Reborn Server Performance"));
        sender.sendMessage(ColorUtils.color("&6&l> &eTPS: &b" + MCUtils.getRecentTps()));

        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        long memoryUsed = totalMemory - freeMemory;
        double percentUsed = memoryUsed / (totalMemory * 1.0) * 100;
        String formattedPercentUsed = String.format("%.2f", percentUsed);

        int memoryUsedMB = (int) MemoryHelper.toMegabytes(memoryUsed);
        int totalMemoryMB = (int) MemoryHelper.toMegabytes(totalMemory);

        sender.sendMessage(ColorUtils.color("&6&l> &eMemory Used: &b" + memoryUsedMB + "MB / " + totalMemoryMB + "MB &7(" + formattedPercentUsed + "%)"));
        sender.sendMessage(ColorUtils.color("&6&l> &eTotal Tasks: &b" + NexusThread.getThreads().size()));
        if (!(args.length > 0) || !args[0].equals("-t")) {
            sender.sendMessage(ColorUtils.color("&6&l> &7Run with the -t flag to see task metrics."));
            return true;
        }

        sender.sendMessage("");
        sender.sendMessage(ColorUtils.color("&6&l>> &d&lRunning Nexus Task Metrics."));
        for (NexusThread<?> task : NexusThread.getThreads()) {
            sender.sendMessage(ColorUtils.color("&6&l> &eName: &b" + task.getClass().getSimpleName() + " &ePlugin: &b" + task.getPlugin().getName()));
            sender.sendMessage(ColorUtils.color("      &ePeriod: &b" + task.getPeriod() + " ticks  &eAsync: &b" + task.isAsync()));
            sender.sendMessage(ColorUtils.color("      &eLowest: &b" + task.getMinTime() + "ms   &eHighest: &b" + task.getMaxTime() + "ms   &eRecent Average: &b" + task.getRecentAverage() + "ms"));
        }

        return true;
    }
}
