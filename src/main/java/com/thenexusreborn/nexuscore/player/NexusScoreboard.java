package com.thenexusreborn.nexuscore.player;

import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.*;

public class NexusScoreboard {
    
    public static final Map<Rank, String> BEGIN_CHARS = new HashMap<>();
    
    static {
        BEGIN_CHARS.put(Rank.DIAMOND, "a");
        BEGIN_CHARS.put(Rank.GOLD, "b");
        BEGIN_CHARS.put(Rank.IRON, "c");
        BEGIN_CHARS.put(Rank.MEMBER, "d");
        BEGIN_CHARS.put(Rank.MEDIA, "e");
        BEGIN_CHARS.put(Rank.VIP, "f");
        BEGIN_CHARS.put(Rank.ARCHITECT, "g");
        BEGIN_CHARS.put(Rank.NEXUS, "h");
        BEGIN_CHARS.put(Rank.ADMIN, "i");
        BEGIN_CHARS.put(Rank.SR_MOD, "j");
        BEGIN_CHARS.put(Rank.MOD, "k");
        BEGIN_CHARS.put(Rank.HELPER, "l");
    }
    
    protected Scoreboard scoreboard;
    protected NexusPlayer player;
    protected ScoreboardView view;
    
    protected final Map<UUID, Team> playerTeams = new HashMap<>();
    
    public NexusScoreboard(NexusPlayer player) {
        this.player = player;
    }
    
    public final Map<UUID, Team> getPlayerTeams() {
        return this.playerTeams;
    }
    
    public Team getTeam(String name) {
        try {
            return scoreboard.getTeam(name);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setView(ScoreboardView view) {
        if (this.view != null) {
            this.view.getObjective().unregister();
            for (String team : this.view.getTeams()) {
                try {
                    Team registeredTeam = this.scoreboard.getTeam(team);
                    for (String entry : registeredTeam.getEntries()) {
                        scoreboard.resetScores(entry);
                    }
                    registeredTeam.unregister();
                } catch (IllegalArgumentException e) {}
            }
        }
        
        if (view != null) {
            Objective objective = view.registerObjective(this.scoreboard);
            view.registerTeams(this.scoreboard, objective);
            view.setObjective(objective);
        }
        this.view = view;
    }
    
    public void update() {
        if (view != null) {
            view.update();
        }
    }
    
    public final void init() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        createPlayerTeam(this.player);
    }
    
    private static String getPlayerTeamName(NexusPlayer player) {
        String pName = player.getName();
        String name = BEGIN_CHARS.get(player.getRank()) + "_";
        if (pName.length() > 13) {
            name += pName.substring(0, 14);
        } else {
            name += pName;
        }
        return name;
    }
    
    public void createPlayerTeam(NexusPlayer nexusPlayer) {
        Team team = scoreboard.registerNewTeam(getPlayerTeamName(nexusPlayer));
        team.addEntry(nexusPlayer.getName());
        setTeamDisplayOptions(nexusPlayer, team);
        this.playerTeams.put(nexusPlayer.getUniqueId(), team);
    }
    
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
    
    public void updatePlayerTeam(NexusPlayer nexusPlayer) {
        Team team = getExistingPlayerTeam(nexusPlayer);
        setTeamDisplayOptions(nexusPlayer, team);
    }
    
    public void refreshPlayerTeam(NexusPlayer nexusPlayer) {
        Team team = getExistingPlayerTeam(nexusPlayer);
        team.unregister();
        createPlayerTeam(nexusPlayer);
    }
    
    private Team getExistingPlayerTeam(NexusPlayer nexusPlayer) {
        Team team = getTeam(BEGIN_CHARS.get(nexusPlayer.getRank()) + "_" + nexusPlayer.getName());
        String playerName;
        if (nexusPlayer.getName().length() > 13) {
            playerName = nexusPlayer.getName().substring(0, 14);
        } else {
            playerName = nexusPlayer.getName();
        }
        if (team == null) {
            for (Team scoreboardTeam : this.scoreboard.getTeams()) {
                if (scoreboardTeam.getName().contains(playerName)) {
                    team = scoreboardTeam;
                    break;
                }
            }
        }
        return team;
    }
    
    private void setTeamDisplayOptions(NexusPlayer nexusPlayer, Team team) {
        if (nexusPlayer.getRank() == Rank.MEMBER) {
            team.setPrefix(MCUtils.color(nexusPlayer.getRank().getColor()));   
        } else {
            team.setPrefix(MCUtils.color(nexusPlayer.getRank().getPrefix() + " &r"));
        }
        if (nexusPlayer.getTag() != null) {
            team.setSuffix(MCUtils.color(" " + nexusPlayer.getTag().getDisplayName()));
        } else {
            team.setSuffix("");
        }
    }
}
