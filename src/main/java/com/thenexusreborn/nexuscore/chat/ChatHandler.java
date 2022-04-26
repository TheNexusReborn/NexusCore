package com.thenexusreborn.nexuscore.chat;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.player.SpigotNexusPlayer;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatHandler {
    boolean handleChat(NexusPlayer player, String chatColor, AsyncPlayerChatEvent e);
}
