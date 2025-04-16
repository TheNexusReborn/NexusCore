package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.api.nickname.Nickname;
import com.thenexusreborn.api.player.NexusPlayer;
import org.bukkit.event.HandlerList;

public class NicknameSetEvent extends NexusPlayerEvent {
    
    private Nickname nickname;
    
    public NicknameSetEvent(NexusPlayer nexusPlayer, Nickname nickname) {
        super(nexusPlayer);
        this.nickname = nickname;
    }
    
    public Nickname getNickname() {
        return nickname;
    }
    
    public static HandlerList getHandlerList()   {
        return handlers;
    }
}
