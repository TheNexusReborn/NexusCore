package com.thenexusreborn.api.sql.objects.objecthandler;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.experience.PlayerExperience;
import com.thenexusreborn.api.nickname.*;
import com.thenexusreborn.api.nickname.player.*;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.sql.objects.*;
import com.thenexusreborn.api.tags.Tag;

import java.sql.SQLException;
import java.util.List;

public class PlayerObjectHandler extends ObjectHandler {
    public PlayerObjectHandler(Object object, SQLDatabase database, Table table) {
        super(object, database, table);
    }
    
    @Override
    public void afterLoad() {
        NexusPlayer player = (NexusPlayer) object;
        player.setPlayerProxy(PlayerProxy.of(player.getUniqueId()));
        
        try {
            PlayerExperience experience = database.get(PlayerExperience.class, "uniqueid", player.getUniqueId().toString()).getFirst();
            player.getExperience().setLevel(experience.getLevel());
            player.getExperience().setLevelXp(experience.getLevelXp());
        } catch (Exception e) {
            if (e instanceof SQLException) {
                e.printStackTrace();
            }
        }
        
        try {
            PlayerTime playerTime = database.get(PlayerTime.class, "uniqueid", player.getUniqueId().toString()).getFirst();
            player.getPlayerTime().setFirstJoined(playerTime.getFirstJoined());
            player.getPlayerTime().setLastLogin(playerTime.getLastLogin());
            player.getPlayerTime().setLastLogout(playerTime.getLastLogout());
            player.getPlayerTime().setPlaytime(playerTime.getPlaytime());
        } catch (Exception e) {
            if (e instanceof SQLException) {
                e.printStackTrace();
            }
        }
        
        try {
            PlayerBalance balance = database.get(PlayerBalance.class, "uniqueid", player.getUniqueId().toString()).getFirst();
            player.getBalance().setCredits(balance.getCredits());
            player.getBalance().setNexites(balance.getNexites());
        } catch (Exception e) {
            if (e instanceof SQLException) {
                e.printStackTrace();
            }
        }
        
        try {
            Nickname nickname = database.get(Nickname.class, "uniqueid", player.getUniqueId().toString()).getFirst();
            if (nickname != null) {
                player.setNickname(nickname);
            }
            
        } catch (Exception e) {
            if (e instanceof SQLException) {
                e.printStackTrace();
            }
        }
        
        try {
            NickExperience nickExperience = database.get(NickExperience.class, "uniqueid", player.getUniqueId().toString()).getFirst();
            if (nickExperience != null) {
                nickExperience.setTrueExperience(player.getTrueExperience());
                if (player.getNickname() == null) {
                    player.setNickname(new Nickname(player.getUniqueId(), null, player.getTrueName(), null, null));
                }
                
                player.getNickname().setFakeExperience(nickExperience);
            }
        } catch (Exception e) {
            if (e instanceof SQLException) {
                e.printStackTrace();
            }
        }
        
        try {
            NickBalance nickBalance = database.get(NickBalance.class, "uniqueid", player.getUniqueId().toString()).getFirst();
            if (nickBalance != null) {
                nickBalance.setTrueBalance(player.getTrueBalance());
                if (player.getNickname() == null) {
                    player.setNickname(new Nickname(player.getUniqueId(), null, player.getTrueName(), null, null));
                }
                
                player.getNickname().setFakeBalance(nickBalance);
            }
        } catch (Exception e) {
            if (e instanceof SQLException) {
                e.printStackTrace();
            }
        }
        
        try {
            NickTime nickTime = database.get(NickTime.class, "uniqueid", player.getUniqueId().toString()).getFirst();
            if (nickTime != null) {
                nickTime.setTrueTime(player.getTrueTime());
                if (player.getNickname() == null) {
                    player.setNickname(new Nickname(player.getUniqueId(), null, player.getTrueName(), null, null));
                }
                
                player.getNickname().setFakeTime(nickTime);
            }
        } catch (Exception e) {
            if (e instanceof SQLException) {
                e.printStackTrace();
            }
        }
        
        try {
            List<Toggle> toggles = database.get(Toggle.class, "uuid", player.getUniqueId());
            player.getToggles().setAll(toggles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
            List<IPEntry> ipEntries = database.get(IPEntry.class, "uuid", player.getUniqueId());
            for (IPEntry ipEntry : ipEntries) {
                player.getIpHistory().add(ipEntry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
            List<Tag> tags = database.get(Tag.class, "uuid", player.getUniqueId());
            player.addAllTags(tags);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void afterSave() {
        NexusPlayer player = (NexusPlayer) object;
        
        if (player.getExperience() != null) {
            database.saveSilent(player.getExperience());
        }
        
        for (Toggle toggle : player.getToggles().findAll()) {
            database.saveSilent(toggle);
        }
        
        for (IPEntry ipEntry : player.getIpHistory()) {
            database.saveSilent(ipEntry);
            NexusAPI.getApi().getPlayerManager().getIpHistory().add(ipEntry);
        }
        
        if (player.getNickname() != null) {
            database.saveSilent(player.getNickname());
            database.saveSilent(player.getNickname().getFakeExperience());
            database.saveSilent(player.getNickname().getFakeBalance());
            database.saveSilent(player.getNickname().getFakeTime());
        } else {
            database.deleteSilent(Nickname.class, player.getUniqueId().toString());
            database.deleteSilent(NickExperience.class, player.getUniqueId().toString(), new Object[]{"persist"}, new Object[]{false});
            database.deleteSilent(NickBalance.class, player.getUniqueId().toString(), new Object[]{"persist"}, new Object[]{false});
            database.deleteSilent(NickTime.class, player.getUniqueId().toString(), new Object[]{"persist"}, new Object[]{false});
        }
    }
}
