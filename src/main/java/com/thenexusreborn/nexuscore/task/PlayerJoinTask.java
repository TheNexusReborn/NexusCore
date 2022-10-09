package com.thenexusreborn.nexuscore.task;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.ScoreboardView;
import com.thenexusreborn.api.util.Response;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.events.NexusPlayerLoadEvent;
import com.thenexusreborn.nexuscore.scoreboard.SpigotNexusScoreboard;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import com.thenexusreborn.nexuscore.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.List;

public class PlayerJoinTask extends BukkitRunnable {

    private NexusCore plugin;
    private Player player;

    public PlayerJoinTask(NexusCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void run() {
        int tries = 1;
        Response<NexusPlayer> response = null;

        while (response == null && tries <= 10) {
            try {
                List<NexusPlayer> players = NexusAPI.getApi().getPrimaryDatabase().get(NexusPlayer.class, "uniqueId", player.getUniqueId().toString());
                if (players.size() == 0) {
                    tries++;
                    continue;
                }
                
                response = new Response<>(players.get(0), Response.Type.SUCCESS);
            } catch (SQLException e) {
                response = new Response<>(Response.Type.FAILURE, e);
            }
        }

        if (response == null && tries == 10) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "Please leave a rejoin. Could not find your player data after 10 tries."));
            return;
        }

        if (!response.success()) {
            player.sendMessage(MCUtils.color(MsgType.WARN + "There was an error while getting your player data. Please file a Bug Report with the following message: &e" + response.getError().getMessage()));
            return;
        }

        NexusPlayer nexusPlayer = response.get();

        if (!nexusPlayer.isOnline()) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                NexusScoreboard scoreboard = new SpigotNexusScoreboard(nexusPlayer);
                scoreboard.init();

                nexusPlayer.setScoreboard(scoreboard);

                NexusPlayerLoadEvent loadEvent = new NexusPlayerLoadEvent(nexusPlayer);
                Bukkit.getServer().getPluginManager().callEvent(loadEvent);

                ScoreboardView scoreboardView = loadEvent.getScoreboardView();
                if (scoreboardView != null) {
                    scoreboard.setView(scoreboardView);
                }

                if (scoreboard.getTablistHandler() == null) {
                    if (loadEvent.getTablistHandler() != null) {
                        scoreboard.setTablistHandler(loadEvent.getTablistHandler());
                    }
                }

                scoreboard.apply();

                String joinMessage = loadEvent.getJoinMessage();
                if (joinMessage != null && !joinMessage.equals("")) {
                    Bukkit.broadcastMessage(MCUtils.color(joinMessage));
                }

                if (nexusPlayer.getRanks().get().ordinal() <= Rank.HELPER.ordinal()) {
                    player.addAttachment(plugin, "spartan.info", true);
                    player.addAttachment(plugin, "spartan.notifications", true);
                }

                NexusAPI.getApi().getPlayerManager().getPlayers().put(nexusPlayer.getUniqueId(), nexusPlayer);

                SpigotUtils.sendActionBar(player, "&aYour data has been loaded");

                if (nexusPlayer.getRanks().get().ordinal() <= Rank.MEDIA.ordinal()) {
                    StaffChat.sendJoin(nexusPlayer);
                }

                if (loadEvent.getActionBar() != null) {
                    new BukkitRunnable() {
                        public void run() {
                            nexusPlayer.setActionBar(loadEvent.getActionBar());
                        }
                    }.runTaskLater(plugin, 60L);
                }
            }
        }.runTask(plugin);
    }

    public void start() {
        runTaskAsynchronously(plugin);
    }
}
