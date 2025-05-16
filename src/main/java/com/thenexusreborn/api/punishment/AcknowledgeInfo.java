package com.thenexusreborn.api.punishment;

public class AcknowledgeInfo {
    private final String code;
    private long time = -1;
    
    public AcknowledgeInfo(String code) {
        this.code = code;
    }
    
    public AcknowledgeInfo(String code, long time) {
        this.code = code;
        this.time = time;
    }
    
    public String getCode() {
        return code;
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "AcknowledgeInfo{" +
                "code='" + code + '\'' +
                ", time=" + time +
                '}';
    }
}
