package com.thenexusreborn.nexuscore.chat;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.player.*;
import com.thenexusreborn.nexuscore.util.MCUtils;
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
        NexusPlayer nexusPlayer = plugin.getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId());
        String chatColor;
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
        
        String message = e.getMessage().replace("%", "%%");
        e.setFormat(MCUtils.color(nexusPlayer.getDisplayName() + "&8: " + chatColor + message));
    }
    
    public void setHandler(ChatHandler handler) {
        this.handler = handler;
    }
    
    public ChatHandler getHandler() {
        return handler;
    }
}
