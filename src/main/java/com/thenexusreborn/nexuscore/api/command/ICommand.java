package com.thenexusreborn.nexuscore.api.command;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface ICommand<T extends JavaPlugin> {
    T getPlugin();
    String getName();
    String[] getAliases();
    String getDescription();
    Rank getMinRank();
    boolean isPlayerOnly();
    
    default void debug(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
            if (nexusPlayer.getToggleValue("debug")) {
                MsgType.VERBOSE.send(sender, message);
            }
        }
    }
}
