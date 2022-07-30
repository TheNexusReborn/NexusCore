package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ToggleCmds implements CommandExecutor {
    
    private final NexusCore plugin;
    
    public ToggleCmds(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }
    
        NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
        Rank minRank = null;
        Preference preference = null;
        
        if (cmd.getName().equalsIgnoreCase("vanish")) {
            minRank = Rank.HELPER;
            preference = player.getPreferences().get("vanish");
        } else if (cmd.getName().equalsIgnoreCase("incognito")) {
            minRank = Rank.MEDIA;
            preference = player.getPreferences().get("incognito");
        }
        
        if (preference == null) {
            player.sendMessage(MsgType.WARN + "Could not find a valid preference. This is a bug, please report to Firestar311.");
            return true;
        }
        
        if (player.getRank().ordinal() > minRank.ordinal()) {
            player.sendMessage(MsgType.WARN + "You do not have enough permission to use that command.");
            return true;
        }
        
        preference.setValue(!preference.getValue());
        String vc = MsgType.INFO.getVariableColor();
        String bc = MsgType.INFO.getBaseColor();
        player.sendMessage(MsgType.INFO + "Toggled " + vc + preference.getInfo().getName() + bc + " to " + vc + preference.getValue());
        NexusAPI.getApi().getThreadFactory().runAsync(() -> NexusAPI.getApi().getDataManager().pushPlayerPreferences(player));
        return true;
    }
}
