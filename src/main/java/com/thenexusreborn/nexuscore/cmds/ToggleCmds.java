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
        } else if (cmd.getName().equalsIgnoreCase("fly")) {
            minRank = Rank.DIAMOND;
            preference = player.getPreferences().get("fly");
        }
        
        if (preference == null) {
            Preference.Info info = NexusAPI.getApi().getPreferenceRegistry().get(cmd.getName().toLowerCase());
            if (info == null) {
                player.sendMessage(MsgType.WARN + "No preference with that type exists.");
                return true;
            }
            
            preference = new Preference(info, player.getUniqueId(), info.getDefaultValue());
        }
        
        if (player.getRank().ordinal() > minRank.ordinal()) {
            player.sendMessage(MsgType.WARN + "You do not have enough permission to use that command.");
            return true;
        }
        
        preference.setValue(!preference.getValue());
        NexusAPI.getApi().getPrimaryDatabase().push(preference);
        String vc = MsgType.INFO.getVariableColor();
        String bc = MsgType.INFO.getBaseColor();
        player.sendMessage(MsgType.INFO + "Toggled " + vc + preference.getInfo().getName() + bc + " to " + vc + preference.getValue());
        return true;
    }
}
