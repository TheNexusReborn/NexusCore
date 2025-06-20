package com.thenexusreborn.api.scoreboard.wrapper;

import com.thenexusreborn.api.scoreboard.ValueUpdater;
import org.bukkit.ChatColor;

import java.util.Set;

public interface ITeam {
    void addEntry(String text);
    
    Set<String> getEntries();
    
    void unregister();
    
    String getName();
    
    void setPrefix(String prefix);
    
    void setSuffix(String suffix);
    
    void setColor(ChatColor chatColor);
    
    ChatColor getColor();
    
    ValueUpdater getValueUpdater();
    
    void setValueUpdater(ValueUpdater valueUpdater);
}
