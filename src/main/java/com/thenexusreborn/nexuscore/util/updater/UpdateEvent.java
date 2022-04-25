package com.thenexusreborn.nexuscore.util.updater;

import org.bukkit.event.*;

public class UpdateEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();

	private UpdateType type;
	private long lastRun, currentRun;

	public UpdateEvent(UpdateType type, long lastRun, long currentRun){
		this.type = type;
		this.lastRun = lastRun;
		this.currentRun = currentRun;
	}

	public UpdateType getType(){
		return type;
	}

	public long getLastRun(){
		return lastRun;
	}

	public static HandlerList getHandlerList(){
		return handlerList;
	}
	
	public long getCurrentRun() {
		return currentRun;
	}
	
	@Override
	public HandlerList getHandlers(){
		return handlerList;
	}
}
