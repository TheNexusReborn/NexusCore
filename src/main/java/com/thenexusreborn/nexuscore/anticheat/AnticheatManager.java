package com.thenexusreborn.nexuscore.anticheat;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.util.MCUtils;
import me.vagdedes.spartan.api.*;
import me.vagdedes.spartan.objects.system.Check;
import org.bukkit.event.*;

public class AnticheatManager implements Listener {
    
    @EventHandler
    public void onPlayerViolation(PlayerViolationEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        if (!e.isFalsePositive()) {
            String category = API.getCategory(e.getPlayer(), e.getHackType());
            if (category.equals(Check.maximumCategory)) {
                if (e.getViolation() % 10 == 0) {
                    StaffChat.sendAnticheat(NexusAPI.getApi().getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId()), e.getHackType().toString(), e.getViolation());
                }
    
                if (e.getViolation() >= 30) {
                    Punishment punishment = new Punishment(System.currentTimeMillis(), 2592000000L, "PowerMoveRegulator", e.getPlayer().getUniqueId().toString(),
                            NexusAPI.getApi().getServerManager().getCurrentServer().getName(), "Cheating", PunishmentType.BAN, Visibility.SILENT);
        
                    NexusAPI.getApi().getPrimaryDatabase().push(punishment);
                    e.getPlayer().kickPlayer(MCUtils.color(punishment.formatKick()));
                }
            }
        }
    }
}
