package com.thenexusreborn.nexuscore.chat;

import com.thenexusreborn.api.player.NexusPlayer;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatHandler {
    boolean handleChat(NexusPlayer player, String chatColor, AsyncPlayerChatEvent e);
}
