package com.thenexusreborn.nexuscore.datatest;

import com.thenexusreborn.api.data.annotations.*;
import com.thenexusreborn.api.data.codec.*;
import com.thenexusreborn.api.tags.Tag;

import java.util.UUID;

/**
 * This is a test class for the database stuff
 */
@TableInfo("profiles")
public class TestProfile {
    @Primary
    private int id;
    
    @ColumnInfo(type = "varchar(36)", notNull = true, codec = UUIDCodec.class) 
    private UUID uniqueId;
    
    private String name;
    
    private int level;
    private long playTime;
    private double xp;
    
    private boolean online;
    
    @ColumnInfo(type = "varchar(100)", codec = TagCodec.class) private Tag tag;
}
