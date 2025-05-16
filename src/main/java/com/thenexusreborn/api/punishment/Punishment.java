package com.thenexusreborn.api.punishment;

import com.stardevllc.helper.StringHelper;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.sql.annotations.column.ColumnCodec;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.column.ColumnType;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.api.sql.objects.codecs.AcknowledgeInfoCodec;
import com.thenexusreborn.api.sql.objects.codecs.PardonInfoCodec;
import com.thenexusreborn.api.util.Constants;

import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
@TableName("punishments")
public class Punishment implements Comparable<Punishment> {
    public static final String KICK_FORMAT = """
            &d&lThe Nexus Reborn &7- {type}
            
            &fStaff: &a{actor}
            &fReason: &b{reason}
            &fExpires: &c{expire}
            &fPunishment ID: &e{id}""";
    
    private long id;
    private long date, length;
    private String actor, target, server, reason;
    private PunishmentType type;
    private Visibility visibility;
    @ColumnType("varchar(1000)")
    @ColumnCodec(PardonInfoCodec.class)
    private PardonInfo pardonInfo;
    @ColumnType("varchar(1000)")
    @ColumnCodec(AcknowledgeInfoCodec.class)
    private AcknowledgeInfo acknowledgeInfo;
    
    //Cache variables
    @ColumnIgnored
    private String actorNameCache, targetNameCache;
    
    private Punishment() {}
    
    public Punishment(long date, long length, String actor, String target, String server, String reason, PunishmentType type, Visibility visibility) {
        this.date = date;
        this.length = length;
        this.actor = actor;
        this.target = target;
        this.server = server;
        this.reason = reason;
        this.type = type;
        this.visibility = visibility;
    }
    
    public Punishment(long date, String actor, String target, String server, String reason, PunishmentType type, Visibility visibility) {
        this(date, 0, actor, target, server, reason, type, visibility);
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public long getDate() {
        return date;
    }
    
    public String getTarget() {
        return target;
    }
    
    public long getLength() {
        return length;
    }
    
    public String getActor() {
        return actor;
    }
    
    public String getServer() {
        return server;
    }
    
    public String getReason() {
        return reason;
    }
    
    public PunishmentType getType() {
        return type;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public PardonInfo getPardonInfo() {
        return pardonInfo;
    }
    
    public void setPardonInfo(PardonInfo pardonInfo) {
        this.pardonInfo = pardonInfo;
    }
    
    public AcknowledgeInfo getAcknowledgeInfo() {
        return acknowledgeInfo;
    }
    
    public void setAcknowledgeInfo(AcknowledgeInfo acknowledgeInfo) {
        this.acknowledgeInfo = acknowledgeInfo;
    }
    
    public boolean isActive() {
        if (this.pardonInfo != null) {
            return false;
        }
    
        if (this.type == PunishmentType.WARN) {
            return !(this.acknowledgeInfo.getTime() > 0);
        }
    
        long timeRemaining = getTimeRemaining();
        if (timeRemaining == -1) {
            return true;
        } else if (timeRemaining > 0) {
            return true;
        }
        
        return System.currentTimeMillis() <= this.date + this.length;
    }
    
    public String getActorNameCache() {
        if (actorNameCache == null) {
            try {
                UUID uuid = UUID.fromString(getActor());
                actorNameCache = NexusAPI.getApi().getPlayerManager().getNameFromUUID(uuid);
                if (actorNameCache == null) {
                    actorNameCache = actor;
                }
            } catch (Exception e) {
                this.actorNameCache = actor;
            }
        }
        
        return actorNameCache;
    }
    
    public void setActorNameCache(String actorNameCache) {
        this.actorNameCache = actorNameCache;
    }
    
    public String getTargetNameCache() {
        if (targetNameCache == null) {
            try {
                UUID uuid = UUID.fromString(getTarget());
                NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
                targetNameCache = nexusPlayer.getName();
                if (targetNameCache == null) {
                    targetNameCache = target;
                }
            } catch (Exception e) {
                this.targetNameCache = target;
            }
        }
        return targetNameCache;
    }
    
    public void setTargetNameCache(String targetNameCache) {
        this.targetNameCache = targetNameCache;
    }
    
    public long getTimeRemaining() {
        if (getLength() == -1) {
            return -1;
        }
        
        long timeRemaining = this.date + this.length - System.currentTimeMillis();
        return timeRemaining > 0 ? timeRemaining : -2;
    }
    
    public String formatTimeLeft() {
        long timeRemaining = getTimeRemaining();
        if (type == PunishmentType.KICK || type == PunishmentType.WARN) {
            return "";
        }
        if (timeRemaining == -1) {
            return "Permanent";
        } else if (timeRemaining == -2) {
            return "Expired";
        } else if (getTimeRemaining() >= 1) {
            return Constants.PUNISHMENT_TIME_FORMAT.format(timeRemaining);
        }
        return "";
    }
    
    public String formatKick() {
        String message = Punishment.KICK_FORMAT;
        message = message.replace("{type}", getType().getColor() + StringHelper.titlize(getType().getVerb()));
        message = message.replace("{actor}", getActorNameCache());
        message = message.replace("{reason}", getReason());
        message = message.replace("{expire}", formatTimeLeft());
        message = message.replace("{id}", getId() + "");
        return message;
    }
    
    @Override
    public String toString() {
        return "Punishment{" +
                "id=" + id +
                ", date=" + date +
                ", length=" + length +
                ", actor='" + actor + '\'' +
                ", target='" + target + '\'' +
                ", server='" + server + '\'' +
                ", reason='" + reason + '\'' +
                ", type=" + type +
                ", visibility=" + visibility +
                ", pardonInfo=" + pardonInfo +
                ", acknowledgeInfo=" + acknowledgeInfo +
                ", active=" + isActive() +
                '}';
    }
    
    @Override
    public int compareTo(Punishment o) {
        return Long.compare(this.id, o.id);
    }
}