package com.thenexusreborn.api.player;

import com.stardevllc.clock.clocks.Stopwatch;
import com.stardevllc.mojang.MojangProfile;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.experience.PlayerExperience;
import com.thenexusreborn.api.nickname.Nickname;
import com.thenexusreborn.api.reward.Reward;
import com.thenexusreborn.api.scoreboard.NexusScoreboard;
import com.thenexusreborn.api.server.NexusServer;
import com.thenexusreborn.api.sql.annotations.column.*;
import com.thenexusreborn.api.sql.annotations.table.TableHandler;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.api.sql.objects.codecs.RanksCodec;
import com.thenexusreborn.api.sql.objects.objecthandler.PlayerObjectHandler;
import com.thenexusreborn.api.tags.Tag;

import java.util.*;

@TableName("players")
@TableHandler(PlayerObjectHandler.class)
public class NexusPlayer implements Comparable<NexusPlayer> {
    
    @PrimaryKey
    protected UUID uniqueId;
    protected String name;
    
    @ColumnIgnored
    protected PlayerExperience experience;
    
    @ColumnIgnored
    protected PlayerTime playerTime;
    
    @ColumnIgnored
    protected PlayerBalance balance;
    
    @ColumnIgnored
    protected Set<IPEntry> ipHistory = new HashSet<>();
    @ColumnType("varchar(1000)")
    @ColumnCodec(RanksCodec.class)
    protected PlayerRanks ranks;
    @ColumnIgnored
    protected PlayerToggles toggles;
    @ColumnIgnored
    protected NexusScoreboard scoreboard;
    @ColumnIgnored
    protected UUID lastMessage;
    @ColumnIgnored
    protected IActionBar actionBar;
    @ColumnIgnored
    protected boolean spokenInChat;
    @ColumnIgnored
    protected PlayerProxy playerProxy;
    @ColumnIgnored
    protected Session session;
    @ColumnIgnored
    protected NexusServer server;
    
    @ColumnIgnored
    protected Stopwatch playTimeStopwatch;
    
    @ColumnIgnored
    private Map<String, Tag> tags = new HashMap<>();
    
    @ColumnIgnored
    private MojangProfile mojangProfile;
    
    @ColumnIgnored
    protected Nickname nickname;
    
    private String activeTag;
    
    private String discordId;
    
    protected NexusPlayer() {
        this(null);
    }
    
    public NexusPlayer(UUID uniqueId) {
        this(0, uniqueId, "");
    }
    
    public NexusPlayer(long id, UUID uniqueId, String name) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.toggles = new PlayerToggles();
        this.ranks = new PlayerRanks(uniqueId);
        this.playerTime = new PlayerTime(uniqueId);
        this.experience = new PlayerExperience(uniqueId);
        this.balance = new PlayerBalance(uniqueId);
    }
    
    public MojangProfile getMojangProfile() {
        return mojangProfile;
    }
    
    public void setMojangProfile(MojangProfile mojangProfile) {
        this.mojangProfile = mojangProfile;
    }
    
    public NexusServer getServer() {
        return server;
    }

    public void setServer(NexusServer server) {
        this.server = server;
    }
    
    public PlayerBalance getTrueBalance() {
        if (balance.getUniqueId() == null) {
            balance.setUniqueId(uniqueId);
        }
        return balance;
    }

    public PlayerBalance getBalance() {
        if (isNicked() && getNickname().getFakeBalance() != null) {
            return getNickname().getFakeBalance();
        }
        
        return getTrueBalance();
    }
    
    public PlayerExperience getTrueExperience() {
        if (this.experience.getUniqueId() == null) {
            experience.setUniqueId(uniqueId);
        }
        return experience;
    }

    public PlayerExperience getExperience() {
        if (isNicked() && getNickname().getFakeExperience() != null) {
            return getNickname().getFakeExperience();
        }
        
        return getTrueExperience();
    }
    
    public PlayerTime getTrueTime() {
        if (this.playerTime.getUniqueId() == null) {
            this.playerTime.setUniqueId(uniqueId);
        }
        return playerTime;
    }

    public PlayerTime getPlayerTime() {
        if (isNicked() && getNickname().getFakeTime() != null) {
            return getNickname().getFakeTime();
        }
        
        return getTrueTime();
    }

    public NexusScoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(NexusScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    public String getTablistName() {
        if (getEffectiveRank() == Rank.MEMBER) {
            return Rank.MEMBER.getColor() + getName();
        } else {
            return "&f" + getName();
        }
    }

    public PlayerProxy getPlayer() {
        if (this.playerProxy == null) {
            this.playerProxy = PlayerProxy.of(this.uniqueId);
        }
        return this.playerProxy;
    }
    
    public void setNickname(Nickname nickname) {
        if (this.nickname != null && nickname != null) {
            this.nickname.copyFrom(nickname);
        } else {
            this.nickname = nickname;
        }
    }
    
    public Nickname getNickname() {
        return nickname;
    }
    
    public boolean isNicked() {
        return nickname != null && nickname.isActive();
    }
    
    public NexusPlayer getLastMessage() {
        return NexusAPI.getApi().getPlayerManager().getNexusPlayer(this.lastMessage);
    }

    public void setLastMessage(NexusPlayer nexusPlayer) {
        this.lastMessage = nexusPlayer.getUniqueId();
    }

    public void setLastMessage(UUID lastMessage) {
        this.lastMessage = lastMessage;
    }

    public IActionBar getActionBar() {
        return actionBar;
    }

    public void setActionBar(IActionBar actionBar) {
        this.actionBar = actionBar;
    }

    public void setSpokenInChat(boolean spokenInChat) {
        this.spokenInChat = spokenInChat;
    }

    public boolean hasSpokenInChat() {
        return this.spokenInChat;
    }

    public void setPlayerProxy(PlayerProxy playerProxy) {
        this.playerProxy = playerProxy;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public long getFirstJoined() {
        return this.playerTime.getFirstJoined();
    }
    
    public void setFirstJoined(long firstJoined) {
        this.playerTime.setFirstJoined(firstJoined);
    }
    
    public long getLastLogin() {
        return this.playerTime.getLastLogin();
    }
    
    public void setLastLogin(long lastLogin) {
        this.playerTime.setLastLogin(lastLogin);
    }
    
    public Rank getEffectiveRank() {
        if (isNicked()) {
            return nickname.getRank();
        } else {
            return getRank();
        }
    }
    
    public String getTrueDisplayName() {
        Rank rank = getRank();
        
        if (rank != Rank.MEMBER) {
            return rank.getPrefix() + " &f" + getTrueName();
        } else {
            return rank.getPrefix() + getTrueName();
        }
    }

    public String getDisplayName() {
        Rank rank = getEffectiveRank();
        
        if (rank != Rank.MEMBER) {
            return rank.getPrefix() + " &f" + getName();
        } else {
            return rank.getPrefix() + getName();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NexusPlayer that = (NexusPlayer) o;
        return Objects.equals(uniqueId, that.uniqueId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
    
    public long getLastLogout() {
        return getPlayerTime().getLastLogout();
    }
    
    public void setLastLogout(long lastLogout) {
        getPlayerTime().setLastLogout(lastLogout);
    }
    
    public void addCredits(int credits) {
        getBalance().addCredits(credits);
    }
    
    public void addXp(double xp) {
        boolean leveledUp = getExperience().addExperience(xp);
        
        if (leveledUp) {
            if (this.playerProxy != null) {
                this.playerProxy.sendMessage("");
                this.playerProxy.sendMessage("&6&l>> &a&lLEVEL UP!");
                this.playerProxy.sendMessage("&6&l>> &e&l" + (getExperience().getLevel() - 1) + " &a-> &e&l" + getExperience().getLevel());
                this.playerProxy.sendMessage("");
                for (Reward reward : NexusAPI.getApi().getLevelManager().getLevel(getExperience().getLevel()).getRewards()) {
                    reward.applyReward(this);
                }
            }
        }
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public String getName() {
        if (isNicked()) {
            return nickname.getName();
        }
        
        if (mojangProfile != null) {
            return mojangProfile.getName();
        }
        
        if (getPlayer() != null) {
            if (getPlayer().getName() != null) {
                return getPlayer().getName();
            }
        }
                
        return name;
    }
    
    public String getTrueName() {
        if (mojangProfile != null) {
            return mojangProfile.getName();
        }
        
        if (nickname != null) {
            return nickname.getTrueName();
        }
        
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<IPEntry> getIpHistory() {
        return ipHistory;
    }
    
    public PlayerRanks getRanks() {
        if (ranks.getUniqueId() == null) {
            ranks.setUniqueId(this.uniqueId);
        }
        return ranks;
    }
    
    public Rank getRank() {
        return getRanks().get();
    }
    
    public void addRank(Rank rank, long expire) {
        getRanks().add(rank, expire);
    }
    
    public void setRank(Rank rank, long expire) {
        getRanks().set(rank, expire);
    }
    
    public void removeRank(Rank rank) {
        getRanks().remove(rank);
    }
    
    public boolean hasRank(Rank rank) {
        return getRanks().contains(rank);
    }
    
    public PlayerToggles getToggles() {
        if (toggles.getUniqueId() == null) {
            toggles.setUniqueId(this.uniqueId);
        }
        return toggles;
    }
    
    public Toggle getToggle(String toggleName) {
        return getToggles().get(toggleName);
    }
    
    public boolean getToggleValue(String toggleName) {
        return getToggles().getValue(toggleName);
    }
    
    public void setToggleValue(String toggleName, boolean value) {
        getToggles().setValue(toggleName, value);
    }
    
    public String getColoredName() {
        return getEffectiveRank().getColor() + getName();
    }
    
    public String getTrueColoredName() {
        return getRank().getColor() + getTrueName();
    }
    
    public void removeCredits(int credits) {
        this.balance.addCredits(-credits);
    }
    
    public boolean isOnline() {
        PlayerProxy player = getPlayer();
        if (player != null) {
            return player.isOnline();
        }
        
        return false;
    }

    public void addToggle(Toggle toggle) {
        getToggles().add(toggle);
    }

    public Tag getActiveTag() {
        return this.tags.get(activeTag);
    }

    public void setActiveTag(String active) {
        if (active == null || active.equalsIgnoreCase("null")) {
            this.activeTag = null;
        } else if (this.tags.containsKey(active.toUpperCase())) {
            this.activeTag = active.toUpperCase();
        }
    }

    public boolean hasActiveTag() {
        return activeTag != null && !activeTag.isEmpty() && !activeTag.equals("null");
    }

    public void addTag(Tag tag) {
        this.tags.put(tag.getName().toUpperCase(), tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag.toUpperCase());
    }

    public void addAllTags(List<Tag> tags) {
        tags.forEach(this::addTag);
    }

    public boolean isTagUnlocked(String tag) {
        return this.tags.containsKey(tag.toUpperCase());
    }

    public Set<String> getTags() {
        return new HashSet<>(this.tags.keySet());
    }

    public Stopwatch getPlayTimeStopwatch() {
        return playTimeStopwatch;
    }

    public void setPlayTimeStopwatch(Stopwatch playTimeStopwatch) {
        this.playTimeStopwatch = playTimeStopwatch;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public void showXPActionBar() {
        if (this.playerProxy != null) {
            this.playerProxy.showXPActionBar();
        }
    }

    @Override
    public int compareTo(NexusPlayer o) {
        return this.getUniqueId().compareTo(o.getUniqueId());
    }
}
