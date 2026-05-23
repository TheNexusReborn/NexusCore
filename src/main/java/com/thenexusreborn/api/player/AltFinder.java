package com.thenexusreborn.api.player;

import com.thenexusreborn.api.NexusReborn;

import java.util.*;

public class AltFinder {
    private static final PlayerManager playerManager = NexusReborn.getPlayerManager();
    
    //This is the base uuid to search for
    private UUID uuid;
    
    //Set of results
    private Map<UUID, Set<String>> results = new HashMap<>();
    
    public AltFinder(UUID uuid) {
        this.uuid = uuid;
    }
    
    private void searchUUID(UUID uuid) {
        Set<String> ips = new HashSet<>();
        for (IPEntry ipEntry : playerManager.getIpHistory()) {
            if (ipEntry.getUuid().equals(uuid)) {
                ips.add(ipEntry.getIp());
            }
        }
        
        this.results.put(uuid, ips);
    }
    
    private void searchIP(String ip) {
        for (IPEntry ipEntry : playerManager.getIpHistory()) {
            if (ipEntry.getIp().equalsIgnoreCase(ip)) {
                results.putIfAbsent(ipEntry.getUuid(), null);
            }
        }
    }
    
    public void search() {
        //Init the results with the base uuid values
        if (this.results.isEmpty()) {
            searchUUID(this.uuid);
        }
        
        //Loop through a copy of the entryset of the results map
        for (Map.Entry<UUID, Set<String>> entry : new HashSet<>(this.results.entrySet())) {
            //If the set of ips is null, that means that it has not been searched, so search that uuid
            if (entry.getValue() == null) {
                searchUUID(entry.getKey());
            }
            
            //Get the list of ips for the uuid, should not be null, can possibly be empty though
            Set<String> ips = this.results.get(entry.getKey());
            for (String ip : ips) {
                searchIP(ip);
            }
        }
        
        //Check to see if we need to continue searching
        boolean searchAgain = false;
        for (Map.Entry<UUID, Set<String>> entry : this.results.entrySet()) {
            if (entry.getValue() == null) {
                searchAgain = true;
                break;
            }
        }
        
        //If so, recursively call the search method until all UUIDs have a non-null set in the map
        if (searchAgain) {
            search();
        }
    }
    
    public Set<UUID> getUuids() {
        return results.keySet();
    }
    
    public Map<UUID, Set<String>> getResults() {
        return results;
    }
}