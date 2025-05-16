package com.thenexusreborn.api.player;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.sql.annotations.column.ColumnCodec;
import com.thenexusreborn.api.sql.annotations.column.ColumnIgnored;
import com.thenexusreborn.api.sql.annotations.column.ColumnName;
import com.thenexusreborn.api.sql.annotations.column.ColumnType;
import com.thenexusreborn.api.sql.annotations.table.TableName;
import com.thenexusreborn.api.sql.objects.codecs.ToggleInfoCodec;

import java.util.*;

@TableName("toggles")
public class Toggle {
    private long id;
    @ColumnName("name")
    @ColumnType("varchar(100)")
    @ColumnCodec(ToggleInfoCodec.class)
    private Info info;
    private UUID uuid;
    private boolean value;
    
    private Toggle() {}
    
    public Toggle(Info info, UUID uuid, boolean value) {
        this.info = info;
        this.value = value;
        this.uuid = uuid;
    }
    
    public Toggle(Info info, UUID uuid, long id, boolean value) {
        this.info = info;
        this.id = id;
        this.value = value;
        this.uuid = uuid;
    }
    
    public boolean getValue() {
        return value;
    }
    
    public void setValue(boolean value) {
        boolean oldValue = this.value;
        this.value = value;
        if (info.handler != null) {
            info.handler.handleChange(this, getPlayer(), oldValue, value);
        }
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public NexusPlayer getPlayer() {
        return NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public Info getInfo() {
        return info;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Toggle that = (Toggle) o;
        return Objects.equals(info, that.info) && Objects.equals(uuid, that.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(info, uuid);
    }
    
    @TableName("toggleinfo")
    public static class Info {
        private long id;
        private String name, displayName, description;
        private Rank minRank;
        private boolean defaultValue;
        @ColumnIgnored
        private Handler handler;
        
        private Info() {}
        
        public Info(String name, Rank minRank, String displayName, String description, boolean defaultValue) {
            this.name = name;
            this.minRank = minRank;
            this.displayName = displayName;
            this.description = description;
            this.defaultValue = defaultValue;
        }
        
        public String getName() {
            return name;
        }
    
        public Rank getMinRank() {
            return minRank;
        }
    
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public boolean getDefaultValue() {
            return defaultValue;
        }
        
        public Handler getHandler() {
            return handler;
        }
        
        public void setHandler(Handler handler) {
            this.handler = handler;
        }

        public void setMinRank(Rank minRank) {
            this.minRank = minRank;
        }

        public void setDefaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Info info = (Info) o;
            return Objects.equals(name, info.name);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
    
    @FunctionalInterface
    public interface Handler {
        void handleChange(Toggle toggle, NexusPlayer player, boolean oldValue, boolean newValue);
    }
}
