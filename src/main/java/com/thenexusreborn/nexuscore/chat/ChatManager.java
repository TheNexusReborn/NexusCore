package com.thenexusreborn.nexuscore.chat;

import com.stardevllc.starchat.api.SpaceChatEvent;
import com.stardevllc.starchat.channels.ChatChannel;
import com.stardevllc.starchat.context.ChatContext;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ChatManager implements Listener {
    private final NexusCore plugin;
    
    public ChatManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSpaceChat(SpaceChatEvent e) {
        ChatContext context = e.getContext();
        
        if (!(context.getSender() instanceof Player)) {
            return;
        }
        
        Player sender = context.getSenderAsPlayer();
        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(sender.getUniqueId());
        
        String message = context.getMessage();
        if (message.startsWith("@")) {
            ChatChannel staffChannel = plugin.getStarChatPlugin().getStaffChannel();
            if (sender.hasPermission(staffChannel.getSendPermission())) {
                staffChannel.sendMessage(new ChatContext(sender, message.substring(1).trim()));
                e.setCancelled(true);
                return;
            }
        }
        
        if (nexusPlayer.getToggleValue("vanish")) {
            if (!e.getChatSpace().getName().equalsIgnoreCase(plugin.getStarChatPlugin().getStaffChannel().getName())) {
                e.setCancelled(true);
                nexusPlayer.sendMessage(MsgType.WARN + "You are not allowed to speak in vanish mode.");
                return;
            }
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
        
        if (nexusPlayer.getRank() == Rank.NEXUS) {
            if (!nexusPlayer.isNicked()) {
                return;
            }
        }
        
        List<Punishment> punishments = NexusReborn.getPunishmentManager().getPunishmentsByTarget(sender.getUniqueId());
        for (Punishment punishment : punishments) {
            if (punishment != null && punishment.isActive()) {
                if (punishment.getType() == PunishmentType.MUTE) {
                    e.setCancelled(true);
                    nexusPlayer.sendMessage(MsgType.WARN + "You are muted, you cannot speak now. (" + punishment.formatTimeLeft() + ")");
                    return;
                } else if (punishment.getType() == PunishmentType.WARN) {
                    if (message.equals(punishment.getAcknowledgeInfo().getCode())) {
                        punishment.getAcknowledgeInfo().setTime(System.currentTimeMillis());
                        nexusPlayer.sendMessage(MsgType.INFO + "You have confirmed your warning. You can speak now.");
                        NexusReborn.getPrimaryDatabase().saveSilent(punishment);
                    } else {
                        e.setCancelled(true);
                        nexusPlayer.sendMessage(MsgType.WARN + "You have an unconfirmed warning, please type the code " + punishment.getAcknowledgeInfo().getCode() + " to confirm.");
                    }
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}