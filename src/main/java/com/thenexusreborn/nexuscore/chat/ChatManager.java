package com.thenexusreborn.nexuscore.chat;

import com.thenexusreborn.api.*;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.punishment.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.ChatColor;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatManager implements Listener {
    private NexusCore plugin;
    
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
    
        Punishment punishment = NexusAPI.getApi().getPunishmentManager().getPunishmentByTarget(nexusPlayer.getUniqueId());
        if (punishment != null && punishment.isActive()) {
            e.setCancelled(true);
            if (punishment.getType() == PunishmentType.MUTE) {
                nexusPlayer.sendMessage(MsgType.WARN + "You are muted. You cannot speak now.");
            } else if (punishment.getType() == PunishmentType.WARN) {
                nexusPlayer.sendMessage(MsgType.WARN + "You have an unconfirmed warning, please type the code " + punishment.getAcknowledgeInfo().getCode() + " to confirm.");
            }
            
            return;
        }
        
        Rank rank = nexusPlayer.getRank();
        if (rank == Rank.NEXUS) {
            chatColor = "&6";
        } else if (rank.ordinal() >= Rank.ADMIN.ordinal() && rank.ordinal() <= Rank.HELPER.ordinal()) {
            chatColor = "&b";
        } else if (rank.ordinal() >= Rank.ARCHITECT.ordinal() && rank.ordinal() <= Rank.IRON.ordinal()) {
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
