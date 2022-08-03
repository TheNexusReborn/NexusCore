package com.thenexusreborn.nexuscore.chat;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.api.util.StaffChat;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ChatManager implements Listener {
    private final NexusCore plugin;
    
    private ChatHandler handler;
    
    public ChatManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId());
        String chatColor;
        
        if (e.isCancelled()) {
            return;
        }
        
        if (e.getMessage().startsWith("@")) {
            if (nexusPlayer.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                StaffChat.sendChat(nexusPlayer, e.getMessage().substring(1));
                e.setCancelled(true);
                return;
            }
        }
        
        List<Punishment> punishments = NexusAPI.getApi().getPunishmentManager().getPunishmentsByTarget(e.getPlayer().getUniqueId());
        for (Punishment punishment : punishments) {
            if (punishment != null && punishment.isActive()) {
                e.setCancelled(true);
                if (punishment.getType() == PunishmentType.MUTE) {
                    nexusPlayer.sendMessage(MsgType.WARN + "You are muted, you cannot speak now. (" + punishment.formatTimeLeft() + ")");
                } else if (punishment.getType() == PunishmentType.WARN) {
                    if (e.getMessage().equals(punishment.getAcknowledgeInfo().getCode())) {
                        punishment.getAcknowledgeInfo().setTime(System.currentTimeMillis());
                        nexusPlayer.sendMessage(MsgType.INFO + "You have confirmed your warning. You can speak now.");
                        NexusAPI.getApi().getDataManager().pushPunishment(punishment);
                        NexusAPI.getApi().getNetworkManager().send("punishment", punishment.getId() + "");
                    } else {
                        nexusPlayer.sendMessage(MsgType.WARN + "You have an unconfirmed warning, please type the code " + punishment.getAcknowledgeInfo().getCode() + " to confirm.");
                    }
                }
                
                return;
            }
        }
        
        if (nexusPlayer.getPreferences().get("vanish").getValue()) {
            e.setCancelled(true);
            nexusPlayer.sendMessage(MsgType.WARN + "You are not allowed to speak in vanish mode.");
            return;
        }
        
        
        if (!nexusPlayer.hasSpokenInChat()) {
            nexusPlayer.setSpokenInChat(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (nexusPlayer.getPreferences().get("incognito").getValue()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showPlayer(Bukkit.getPlayer(nexusPlayer.getUniqueId()));
                        }
                    }
                }
            }.runTask(plugin);
        }
        
        Rank rank = nexusPlayer.getRank();
        if (rank == Rank.NEXUS) {
            chatColor = "&6";
        } else if (rank.ordinal() >= Rank.ADMIN.ordinal() && rank.ordinal() <= Rank.HELPER.ordinal()) {
            chatColor = "&b";
        } else if (rank.ordinal() >= Rank.VIP.ordinal() && rank.ordinal() <= Rank.IRON.ordinal()) {
            chatColor = "&f";
        } else {
            chatColor = "&7";
        }
        
        if (handler != null) {
            if (handler.handleChat(nexusPlayer, chatColor, e)) {
                e.setCancelled(true);
                return;
            }
        }
        
        String format = "&8(&2&l{level}&8) &r" + nexusPlayer.getDisplayName() + "{tag}&8: " + chatColor + ChatColor.stripColor(MCUtils.color(e.getMessage()));
        format = format.replace("{level}", nexusPlayer.getLevel() + "");
        if (nexusPlayer.getTag() != null) {
            format = format.replace("{tag}", " " + nexusPlayer.getTag().getDisplayName());
        } else {
            format = format.replace("{tag}", "");
        }
        e.setFormat(MCUtils.color(format.replace("%", "%%")));
    }
    
    public void setHandler(ChatHandler handler) {
        this.handler = handler;
    }
    
    public ChatHandler getHandler() {
        return handler;
    }
}
