package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.data.objects.Database;
import com.thenexusreborn.api.stats.*;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.api.tournament.Tournament.ScoreInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class TournamentScoreTask extends BukkitRunnable {
    
    private JavaPlugin plugin;
    
    public TournamentScoreTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        Tournament tournament = NexusAPI.getApi().getTournament();
        if (tournament == null) {
            return;
        }
        
        Database database = NexusAPI.getApi().getPrimaryDatabase();
    
        Map<UUID, Integer> scores = new HashMap<>();
        try {
            List<Stat> stats = database.get(Stat.class, "name", "sg_tournament_points");
            for (Stat stat : stats) {
                UUID uuid = stat.getUuid();
                int value = (int) stat.getValue();
                if (value != 0) {
                    scores.put(uuid, value);
                }
            }
            
            List<StatChange> statChanges = database.get(StatChange.class, "name", "sg_tournament_points");
            for (StatChange stat : statChanges) {
                UUID uuid = stat.getUuid();
                int value = (int) stat.getValue();
                if (value != 0) {
                    if (scores.containsKey(uuid)) {
                        scores.put(uuid, scores.get(uuid) + value);
                    } else {
                        scores.put(uuid, value);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while getting stats on tournament stat update task: " + e.getMessage());
            scores.clear();
            return;
        }
        
        Iterator<Entry<UUID, Integer>> iterator = scores.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<UUID, Integer> entry = iterator.next();
            ScoreInfo scoreInfo = tournament.getScoreCache().get(entry.getKey());
            if (scoreInfo != null) {
                scoreInfo.setScore(entry.getValue());
                scoreInfo.setLastUpdated(System.currentTimeMillis());
                iterator.remove();
            }
        }
        
        for (Entry<UUID, Integer> entry : scores.entrySet()) {
            String name;
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                name = player.getName();
            } else {
                name = NexusAPI.getApi().getPlayerManager().getCachedPlayer(entry.getKey()).getName();
            }
            
            if (name != null) {
                ScoreInfo scoreInfo = new ScoreInfo(entry.getKey(), name, entry.getValue());
                scoreInfo.setLastUpdated(System.currentTimeMillis());
                tournament.getScoreCache().put(scoreInfo.getUuid(), scoreInfo);
            }
        }
        
        scores.clear();
    }
    
    public void start() {
        runTaskTimerAsynchronously(plugin, 20L, 1200L);
    }
}
