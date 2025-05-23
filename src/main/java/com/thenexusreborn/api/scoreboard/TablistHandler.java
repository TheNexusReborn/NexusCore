package com.thenexusreborn.api.scoreboard;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;

import java.util.*;
import java.util.logging.Logger;

public abstract class TablistHandler {
    protected final NexusScoreboard scoreboard;
    protected final Map<UUID, ITeam> playerTeams = new HashMap<>();
    
    public TablistHandler(NexusScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public abstract void update();
    public abstract void unregister();
    public abstract String getPlayerTeamName(NexusPlayer player);
    public abstract void setDisplayOptions(NexusPlayer nexusPlayer, ITeam team);
    public void updatePlayerTeam(NexusPlayer nexusPlayer) {
        ITeam team = getExistingPlayerTeam(nexusPlayer);
        setDisplayOptions(nexusPlayer, team);
    }
    
    public void refreshPlayerTeam(NexusPlayer nexusPlayer) {
        ITeam team = getExistingPlayerTeam(nexusPlayer);
        if (team != null) {
            team.unregister();
        }
        createPlayerTeam(nexusPlayer);
    }
    
    public ITeam getExistingPlayerTeam(NexusPlayer nexusPlayer) {
        return playerTeams.get(nexusPlayer.getUniqueId());
    }
    
    public Map<UUID, ITeam> getPlayerTeams() {
        return playerTeams;
    }
    
    public ITeam createPlayerTeam(NexusPlayer nexusPlayer) {
        try {
            String playerTeamName = getPlayerTeamName(nexusPlayer);
            ITeam team = scoreboard.registerNewTeam(playerTeamName);
            team.addEntry(nexusPlayer.getName());
            setDisplayOptions(nexusPlayer, team);
            this.playerTeams.put(nexusPlayer.getUniqueId(), team);
            return team;
        } catch (Exception e) {
//            NexusReborn.getLogger().severe("Error while creating a player team: " + e.getMessage());
        }
        
        return null;
    }
    
    public abstract Logger getLogger();
}
