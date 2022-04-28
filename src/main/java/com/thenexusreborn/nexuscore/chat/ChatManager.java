package com.thenexusreborn.nexuscore.chat;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
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
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(e.getPlayer().getUniqueId());
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
        
        String format = "&8(&2&l{level}&8) &r" + nexusPlayer.getDisplayName() + "&8: " + chatColor + e.getMessage().replace("%", "%%");
        format = format.replace("{level}", nexusPlayer.getLevel() + "");
        e.setFormat(MCUtils.color(format));
    }
    
    public void setHandler(ChatHandler handler) {
        this.handler = handler;
    }
    
    public ChatHandler getHandler() {
        return handler;
    }
}
