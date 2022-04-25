package com.thenexusreborn.nexuscore.util.updater;

public enum UpdateType {
    TICK(50L),
    QUARTER_SEC(50L * 5L),
    SECOND(50L * 20L);
    
    private final long length;
    private long lastRun = 0L, currentRun = 0L;
    
    UpdateType(long length) {
        this.length = length;
    }
    
    public long getLength() {
        return length;
    }
    
    public boolean run() {
        if ((System.currentTimeMillis() - lastRun) > length) {
            this.currentRun = System.currentTimeMillis();
            lastRun = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    
    public long getLastRun() {
        return lastRun;
    }
    
    public long getCurrentRun() {
        return currentRun;
    }
}
