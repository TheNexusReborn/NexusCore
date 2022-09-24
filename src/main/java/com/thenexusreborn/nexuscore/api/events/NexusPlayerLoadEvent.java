package com.thenexusreborn.nexuscore.api.events;

import com.thenexusreborn.api.player.IActionBar;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.scoreboard.ScoreboardView;
import com.thenexusreborn.api.scoreboard.TablistHandler;
import org.bukkit.event.HandlerList;

public class NexusPlayerLoadEvent extends NexusPlayerEvent {
    
    private static final HandlerList handlers = new HandlerList();
    
    private String joinMessage;
    private ScoreboardView scoreboardView;
    private TablistHandler tablistHandler;
    private IActionBar actionBar;

    public NexusPlayerLoadEvent(NexusPlayer nexusPlayer) {
        super(nexusPlayer);
    }
    
    public String getJoinMessage() {
        return joinMessage;
    }
    
    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }

    public ScoreboardView getScoreboardView() {
        return scoreboardView;
    }

    public void setScoreboardView(ScoreboardView scoreboardView) {
        this.scoreboardView = scoreboardView;
    }

    public TablistHandler getTablistHandler() {
        return tablistHandler;
    }

    public void setTablistHandler(TablistHandler tablistHandler) {
        this.tablistHandler = tablistHandler;
    }

    public IActionBar getActionBar() {
        return actionBar;
    }

    public void setActionBar(IActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList()   {
        return handlers;
    }
}
