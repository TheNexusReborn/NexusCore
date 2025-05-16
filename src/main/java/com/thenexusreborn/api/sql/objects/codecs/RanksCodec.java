package com.thenexusreborn.api.sql.objects.codecs;

import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.sql.objects.SqlCodec;

import java.util.*;
import java.util.Map.Entry;

public class RanksCodec implements SqlCodec<PlayerRanks> {
    @Override
    public String encode(Object object) {
        Map<Rank, Long> ranks = ((PlayerRanks) object).findAll();
        
        StringBuilder sb = new StringBuilder();
    
        if (ranks.isEmpty()) {
            return Rank.MEMBER.name() + "=-1";
        }
    
        for (Entry<Rank, Long> entry : ranks.entrySet()) {
            sb.append(entry.getKey().name()).append("=").append(entry.getValue()).append(",");
        }
    
        if (!sb.isEmpty()) {
            return sb.substring(0, sb.toString().length() - 1);
        } else {
            return "";
        }
    }
    
    @Override
    public PlayerRanks decode(String encoded) {
        PlayerRanks playerRanks = new PlayerRanks(null);
        if (encoded == null || encoded.isEmpty()) {
            return playerRanks;
        }
    
        String[] rawRanks = encoded.split(",");

        for (String rawRank : rawRanks) {
            String[] rankSplit = rawRank.split("=");
            if (rankSplit.length != 2) {
                continue;
            }
        
            Rank rank = Rank.parseRank(rankSplit[0]);
            long expire = Long.parseLong(rankSplit[1]);
            playerRanks.add(rank, expire);
        }
        return playerRanks;
    }
}
