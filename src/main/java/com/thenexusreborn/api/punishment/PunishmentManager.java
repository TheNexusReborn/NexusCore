package com.thenexusreborn.api.punishment;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.IPEntry;

import java.util.*;

public class PunishmentManager {
    
    private final Map<Long, Punishment> punishments = new HashMap<>();
    
    public void addPunishment(Punishment punishment) {
        this.punishments.put(punishment.getId(), punishment);
    }
    
    public Punishment getPunishment(long id) {
        return punishments.get(id);
    }
    
    public Punishment getPunishmentByTarget(UUID target) {
        for (Punishment punishment : this.punishments.values()) {
            if (punishment.getTarget().equalsIgnoreCase(target.toString())) {
                return punishment;
            }
        }
        
        return null;
    }
    
    public List<Punishment> getPunishmentsByTarget(UUID target) {
        List<Punishment> punishments = new ArrayList<>();
    
        Set<IPEntry> ipHistory = new HashSet<>(NexusAPI.getApi().getPlayerManager().getIpHistory());
        List<String> methodTargetIPs = getIpHistory(ipHistory, target);
    
        for (Punishment punishment : this.punishments.values()) {
            UUID punishmentTarget = UUID.fromString(punishment.getTarget());
            if (punishmentTarget.equals(target)) {
                punishments.add(punishment);
            } else if (punishment.getType() == PunishmentType.BLACKLIST) {
                List<String> punishmentTargetIPs = getIpHistory(ipHistory, punishmentTarget);
                List<String> sharedIps = new ArrayList<>();
                for (String methodTargetIP : methodTargetIPs) {
                    for (String punishmentTargetIP : punishmentTargetIPs) {
                        if (methodTargetIP.equalsIgnoreCase(punishmentTargetIP)) {
                            sharedIps.add(methodTargetIP);
                        }
                    }
                }
    
                if (!sharedIps.isEmpty()) {
                    punishments.add(punishment);
                }
            }
        }
        
        return punishments;
    }
    
    private List<String> getIpHistory(Set<IPEntry> ipHistory, UUID punishmentTarget) {
        List<String> ips = new ArrayList<>();
        for (IPEntry entry : ipHistory) {
            if (entry.getUuid().equals(punishmentTarget)) {
                ips.add(entry.getIp());
            }
        }
        return ips;
    }
    
    public List<Punishment> getPunishments() {
        return new ArrayList<>(this.punishments.values());
    }
}
