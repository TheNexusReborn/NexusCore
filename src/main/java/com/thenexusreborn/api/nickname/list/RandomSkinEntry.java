package com.thenexusreborn.api.nickname.list;

import com.thenexusreborn.api.sql.annotations.column.ColumnType;
import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

@TableName("randomskins")
public class RandomSkinEntry {
    @PrimaryKey
    @ColumnType("varchar(40)")
    private String name;
    
    private RandomSkinEntry() {}
    
    public RandomSkinEntry(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}