package com.thenexusreborn.nexuscore.anticheat;

import com.thenexusreborn.api.*;
import me.vagdedes.spartan.api.*;
import org.bukkit.Bukkit;
import org.bukkit.event.*;

public class AnticheatManager implements Listener {
    
    @EventHandler
    public void onPlayerViolation(PlayerViolationEvent e) {
        if (!e.isFalsePositive()) {
            if (e.getViolation() % 10 == 0) {
                StaffChat.sendAnticheat(NexusAPI.getApi().getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId()), e.getHackType().toString(), e.getViolation());
            }
    
            if (e.getViolation() >= 30 || API.getVL(e.getPlayer()) >= 30) {
                if (e.getViolation() >= 30) {
                    NexusAPI.getApi().getLogger().info("Banned for a single violation count");
                } else {
                    NexusAPI.getApi().getLogger().info("Banned for multiple violation counts");
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + e.getPlayer().getName() + " 30d Cheating");
            }
        }
    }
}
