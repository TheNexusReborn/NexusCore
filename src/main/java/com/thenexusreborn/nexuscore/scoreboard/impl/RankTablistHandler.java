package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.stardevllc.starcore.StarColors;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class RankTablistHandler extends TablistHandler {
    
    public static final Map<Rank, String> BEGIN_CHARS = new HashMap<>();
    
    static {
        BEGIN_CHARS.put(Rank.NEXUS, "a");
        BEGIN_CHARS.put(Rank.ADMIN, "b");
        BEGIN_CHARS.put(Rank.HEAD_MOD, "c");
        BEGIN_CHARS.put(Rank.SR_MOD, "d");
        BEGIN_CHARS.put(Rank.MOD, "e");
        BEGIN_CHARS.put(Rank.HELPER, "f");
        BEGIN_CHARS.put(Rank.MVP, "g");
        BEGIN_CHARS.put(Rank.VIP, "h");
        BEGIN_CHARS.put(Rank.ARCHITECT, "i");
        BEGIN_CHARS.put(Rank.MEDIA, "j");
        BEGIN_CHARS.put(Rank.PLATINUM, "k");
        BEGIN_CHARS.put(Rank.DIAMOND, "l");
        BEGIN_CHARS.put(Rank.BRASS, "m");
        BEGIN_CHARS.put(Rank.GOLD, "n");
        BEGIN_CHARS.put(Rank.INVAR, "o");
        BEGIN_CHARS.put(Rank.IRON, "p");
        BEGIN_CHARS.put(Rank.MEMBER, "q");
    }
    
    public RankTablistHandler(NexusScoreboard scoreboard) {
        super(scoreboard);
    }
    
    @Override
    public void update() {
        removeDisconnectedPlayers();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer != null) {
                ITeam team = getPlayerTeams().get(nexusPlayer.getUniqueId());
                String correctChar = BEGIN_CHARS.get(nexusPlayer.getEffectiveRank());
                if (team == null) {
                    createPlayerTeam(nexusPlayer);
                } else {
                    if (team.getName().startsWith(correctChar)) {
                        updatePlayerTeam(nexusPlayer);
                    } else {
                        refreshPlayerTeam(nexusPlayer);
                    }
                }
            }
        }
    }
    
    public void removeDisconnectedPlayers() {
        Iterator<Map.Entry<UUID, ITeam>> teamIterator = this.playerTeams.entrySet().iterator();
        while (teamIterator.hasNext()) {
            Map.Entry<UUID, ITeam> entry = teamIterator.next();
            if (Bukkit.getPlayer(entry.getKey()) == null) {
                entry.getValue().unregister();
                teamIterator.remove();
            }
        }
    }
    
    @Override
    public void unregister() {
        for (ITeam team : this.playerTeams.values()) {
            team.unregister();
        }
    }
    
    @Override
    public String getPlayerTeamName(NexusPlayer player) {
        String pName = player.getName();
        String name = BEGIN_CHARS.get(player.getEffectiveRank()) + "_";
        if (pName.length() > 13) {
            name += pName.substring(0, 14);
        } else {
            name += pName;
        }
        return name;
    }
    
    @Override
    public void setDisplayOptions(NexusPlayer nexusPlayer, ITeam team) {
        if (nexusPlayer.getEffectiveRank() == Rank.MEMBER) {
            team.setPrefix(StarColors.color(nexusPlayer.getEffectiveRank().getColor()));
        } else {
            team.setPrefix(StarColors.color(nexusPlayer.getEffectiveRank().getPrefix() + " &r"));
        }
        if (nexusPlayer.hasActiveTag()) {
            team.setSuffix(StarColors.color(" " + nexusPlayer.getActiveTag().getDisplayName()));
        } else {
            team.setSuffix("");
        }
    }
}
