package com.thenexusreborn.nexuscore.api.command;

import com.thenexusreborn.api.player.Rank;
import org.bukkit.plugin.java.JavaPlugin;

public interface ICommand<T extends JavaPlugin> {
    T getPlugin();
    String getName();
    String[] getAliases();
    String getDescription();
    Rank getMinRank();
    boolean isPlayerOnly();
}
