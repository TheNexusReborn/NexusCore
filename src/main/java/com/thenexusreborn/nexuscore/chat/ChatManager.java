package com.thenexusreborn.nexuscore.chat;

import com.stardevllc.starchat.channels.ChatChannel;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ChatManager implements Listener {
    private final NexusCore plugin;
    
    public ChatManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId());
        
        if (e.isCancelled()) {
            return;
        }
        
        if (e.getMessage().startsWith("@")) {
            ChatChannel staffChannel = plugin.getStarChatPlugin().getStaffChannel();
            if (e.getPlayer().hasPermission(staffChannel.getSendPermission())) {
                staffChannel.sendMessage(e.getPlayer(), e.getMessage().substring(1));
                e.setCancelled(true);
                return;
            }
        }
        
        List<Punishment> punishments = NexusAPI.getApi().getPunishmentManager().getPunishmentsByTarget(e.getPlayer().getUniqueId());
        for (Punishment punishment : punishments) {
            if (punishment != null && punishment.isActive()) {
                if (punishment.getType() == PunishmentType.MUTE) {
                    e.setCancelled(true);
                    nexusPlayer.sendMessage(MsgType.WARN + "You are muted, you cannot speak now. (" + punishment.formatTimeLeft() + ")");
                    return;
                } else if (punishment.getType() == PunishmentType.WARN) {
                    if (e.getMessage().equals(punishment.getAcknowledgeInfo().getCode())) {
                        punishment.getAcknowledgeInfo().setTime(System.currentTimeMillis());
                        nexusPlayer.sendMessage(MsgType.INFO + "You have confirmed your warning. You can speak now.");
                        NexusAPI.getApi().getPrimaryDatabase().saveSilent(punishment);
                        NexusAPI.getApi().getNetworkManager().send("punishment", punishment.getId() + "");
                    } else {
                        e.setCancelled(true);
                        nexusPlayer.sendMessage(MsgType.WARN + "You have an unconfirmed warning, please type the code " + punishment.getAcknowledgeInfo().getCode() + " to confirm.");
                    }
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        if (nexusPlayer.getToggleValue("vanish")) {
            e.setCancelled(true);
            nexusPlayer.sendMessage(MsgType.WARN + "You are not allowed to speak in vanish mode.");
            return;
        }
        
        
        if (!nexusPlayer.hasSpokenInChat()) {
            nexusPlayer.setSpokenInChat(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (nexusPlayer.getToggleValue("incognito")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showPlayer(Bukkit.getPlayer(nexusPlayer.getUniqueId()));
                        }
                    }
                }
            }.runTask(plugin);
        }
    }
}