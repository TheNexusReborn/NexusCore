package com.thenexusreborn.api.nickname.list;

import com.thenexusreborn.api.sql.annotations.column.ColumnType;
import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

@TableName("randomnames")
public class RandomNameEntry {
    @PrimaryKey
    @ColumnType("varchar(32)")
    private String name;
    
    private RandomNameEntry() {}
    
    public RandomNameEntry(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}