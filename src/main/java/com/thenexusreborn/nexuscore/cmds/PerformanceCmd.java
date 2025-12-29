package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.utils.StarThread;
import com.stardevllc.starlib.units.MemoryUnit;
import com.stardevllc.starmclib.command.flags.FlagResult;
import com.stardevllc.starmclib.command.flags.type.PresenceFlag;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.CommandSender;

public class PerformanceCmd extends NexusCommand<NexusCore> {

    public static final PresenceFlag THREADS_FLAG = new PresenceFlag("t", "Threads");
    
    public PerformanceCmd(NexusCore plugin) {
        super(plugin, "tps", "", Rank.MEMBER, "performance", "memory", "mem", "perf");
        cmdFlags.addFlag(THREADS_FLAG);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        sender.sendMessage(StarColors.color("&6&l>> &d&lThe Nexus Reborn Server Performance"));
        sender.sendMessage(StarColors.color("&6&l> &eTPS: &b" + MCUtils.getRecentTps()));

        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        long memoryUsed = totalMemory - freeMemory;
        double percentUsed = memoryUsed / (totalMemory * 1.0) * 100;
        String formattedPercentUsed = String.format("%.2f", percentUsed);

        int memoryUsedMB = (int) MemoryUnit.BYTE.toMegabytes(memoryUsed);
        int totalMemoryMB = (int) MemoryUnit.BYTE.toMegabytes(totalMemory);

        sender.sendMessage(StarColors.color("&6&l> &eMemory Used: &b" + memoryUsedMB + "MB / " + totalMemoryMB + "MB &7(" + formattedPercentUsed + "%)"));
        sender.sendMessage(StarColors.color("&6&l> &eTotal Tasks: &b" + StarThread.THREADS.size()));
        if (!flagResults.isPresent(THREADS_FLAG)) {
            sender.sendMessage(StarColors.color("&6&l> &7Run with the -t flag to see task metrics."));
            return true;
        }

        sender.sendMessage("");
        sender.sendMessage(StarColors.color("&6&l>> &d&lRunning Nexus Task Metrics."));
        for (StarThread<?> task : StarThread.THREADS) {
            sender.sendMessage(StarColors.color("&6&l> &eName: &b" + task.getClass().getSimpleName() + " &ePlugin: &b" + task.getPlugin().getName()));
            sender.sendMessage(StarColors.color("      &ePeriod: &b" + task.getPeriod() + " ticks  &eAsync: &b" + task.isAsync()));
            sender.sendMessage(StarColors.color("      &eLowest: &b" + task.getMinTime() + "ms   &eHighest: &b" + task.getMaxTime() + "ms   &eRecent Average: &b" + task.getRecentAverage() + "ms"));
        }

        return true;
    }
}
