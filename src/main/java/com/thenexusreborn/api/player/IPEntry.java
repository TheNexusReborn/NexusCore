package com.thenexusreborn.api.player;

import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.*;

@TableName("iphistory")
public class IPEntry {
    
    private long id;
    private String ip;
    private UUID uuid;
    
    private IPEntry() {}
    
    public IPEntry(String ip, UUID uuid) {
        this.ip = ip;
        this.uuid = uuid;
    }
    
    public String getIp() {
        return ip;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IPEntry ipEntry = (IPEntry) o;
        return Objects.equals(ip, ipEntry.ip) && Objects.equals(uuid, ipEntry.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ip, uuid);
    }
}
