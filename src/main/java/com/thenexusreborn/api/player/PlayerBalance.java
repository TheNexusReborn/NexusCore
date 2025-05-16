package com.thenexusreborn.api.player;

import com.thenexusreborn.api.sql.annotations.column.PrimaryKey;
import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.UUID;

@TableName("balances")
public class PlayerBalance {
    @PrimaryKey
    private UUID uniqueId;
    protected double nexites, credits;
    
    protected PlayerBalance() {}

    public PlayerBalance(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public double getNexites() {
        return nexites;
    }
    
    public double addNexites(double nexites) {
        this.nexites += nexites;
        return this.nexites;
    }

    public double getCredits() {
        return credits;
    }
    
    public double addCredits(double credits) {
        this.credits += credits;
        return this.credits;
    }

    public void setNexites(double nexites) {
        this.nexites = nexites;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}