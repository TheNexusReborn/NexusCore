package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.scoreboard.*;
import com.thenexusreborn.api.scoreboard.wrapper.ITeam;
import com.thenexusreborn.nexuscore.util.MCUtils;
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
        BEGIN_CHARS.put(Rank.VIP, "g");
        BEGIN_CHARS.put(Rank.ARCHITECT, "h");
        BEGIN_CHARS.put(Rank.MEDIA, "i");
        BEGIN_CHARS.put(Rank.PLATINUM, "j");
        BEGIN_CHARS.put(Rank.DIAMOND, "k");
        BEGIN_CHARS.put(Rank.BRASS, "l");
        BEGIN_CHARS.put(Rank.GOLD, "m");
        BEGIN_CHARS.put(Rank.INVAR, "n");
        BEGIN_CHARS.put(Rank.IRON, "o");
        BEGIN_CHARS.put(Rank.MEMBER, "p");
    }
    
    public RankTablistHandler(NexusScoreboard scoreboard) {
        super(scoreboard);
    }
    
    @Override
    public void update() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            NexusPlayer otherNexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(other.getUniqueId());
            if (otherNexusPlayer != null) {
                NexusPlayer player = scoreboard.getPlayer();
                ITeam otherTeam = getPlayerTeams().get(otherNexusPlayer.getUniqueId());
                String correctChar = BEGIN_CHARS.get(otherNexusPlayer.getRank());
                if (otherTeam == null) {
                    createPlayerTeam(otherNexusPlayer);
                } else {
                    if (otherTeam.getName().startsWith(correctChar)) {
                        updatePlayerTeam(otherNexusPlayer);
                    } else {
                        refreshPlayerTeam(otherNexusPlayer);
                    }
                }
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
        String name = BEGIN_CHARS.get(player.getRank()) + "_";
        if (pName.length() > 13) {
            name += pName.substring(0, 14);
        } else {
            name += pName;
        }
        return name;
    }
    
    @Override
    public void setDisplayOptions(NexusPlayer nexusPlayer, ITeam team) {
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
