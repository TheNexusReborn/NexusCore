package com.thenexusreborn.api.tags;

import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.*;

@TableName("unlockedtags")
public class Tag {
    
    private long id;
    private UUID uuid;
    private String name;
    private long timestamp;

    private Tag() {}
    
    public Tag(UUID uuid, String name, long timestamp) {
        this.uuid = uuid;
        this.name = name;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDisplayName() {
        if (this.name != null) {
            return "&d&l" + this.name.toUpperCase();
        } else {
            return "";
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
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
