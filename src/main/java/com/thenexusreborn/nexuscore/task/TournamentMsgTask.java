package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TournamentMsgTask extends BukkitRunnable {
    
    private JavaPlugin plugin;
    
    public TournamentMsgTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Tournament tournament = NexusAPI.getApi().getTournament();
        if (tournament != null && tournament.isActive()) {
            Bukkit.broadcastMessage(MCUtils.color("&6&l>> &aThere is an active tournament going on right now."));
            Bukkit.broadcastMessage(MCUtils.color("&6&l> &aYou will be seeing some additional messages for Points in chat."));
            Bukkit.broadcastMessage(MCUtils.color("&6&l> &aYou can use &b/tournament (alias /tt) leaderboard &ato see the current leaderboards"));
            Bukkit.broadcastMessage(MCUtils.color("&6&l> &aYou can use &b/tournament score &ato see your score specifically"));
        }
    }
    
    public void start() {
        runTaskTimer(plugin, 20L, 2450L);
    }
}
