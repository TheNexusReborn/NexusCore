package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Toggle;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.api.events.ToggleChangeEvent;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCmds implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }
    
        NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
        Toggle toggle = player.getToggle(cmd.getName().toLowerCase());
        
        if (toggle == null) {
            Toggle.Info info = NexusAPI.getApi().getToggleRegistry().get(cmd.getName().toLowerCase());
            if (info == null) {
                player.sendMessage(MsgType.WARN + "No toggle with that name exists.");
                return true;
            }
    
            toggle = new Toggle(info, player.getUniqueId(), info.getDefaultValue());
            player.addToggle(toggle);
        }
        
        if (player.getRank().ordinal() > toggle.getInfo().getMinRank().ordinal()) {
            player.sendMessage(MsgType.WARN + "You do not have enough permission to use that toggle.");
            return true;
        }
    
        ToggleChangeEvent changeEvent = new ToggleChangeEvent(player, toggle, toggle.getValue(), !toggle.getValue());
        Bukkit.getPluginManager().callEvent(changeEvent);
        
        if (changeEvent.isCancelled()) {
            if (changeEvent.getCancelReason() != null && !changeEvent.getCancelReason().equals("")) {
                player.sendMessage(MsgType.WARN + changeEvent.getCancelReason());
            } else {
                player.sendMessage(MsgType.WARN + "Changes were cancelled without a reason.");
            }
            return true;
        }
        
        toggle.setValue(!toggle.getValue());
        NexusAPI.getApi().getPrimaryDatabase().push(toggle);
        String vc = MsgType.INFO.getVariableColor();
        String bc = MsgType.INFO.getBaseColor();
        player.sendMessage(MsgType.INFO + "Toggled " + vc + toggle.getInfo().getName() + bc + " to " + vc + toggle.getValue());
        return true;
    }
}
