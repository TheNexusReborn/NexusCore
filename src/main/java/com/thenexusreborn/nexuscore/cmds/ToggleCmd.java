package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.api.events.ToggleChangeEvent;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

//The plugin type is just a generic JavaPlugin to allow other plugins to use this class
public class ToggleCmd extends NexusCommand<JavaPlugin> {

    public ToggleCmd(JavaPlugin plugin, String name, String... aliases) {
        super(plugin, name, "", Rank.MEMBER, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StarColors.color(MsgType.WARN + "Only players can use that command."));
            return true;
        }

        NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
        Toggle toggle = player.getToggle(getName().toLowerCase());

        if (toggle == null) {
            Toggle.Info info = NexusAPI.getApi().getToggleRegistry().get(getName().toLowerCase());
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
            if (changeEvent.getCancelReason() != null && !changeEvent.getCancelReason().isEmpty()) {
                player.sendMessage(MsgType.WARN + changeEvent.getCancelReason());
            } else {
                player.sendMessage(MsgType.WARN + "Changes were cancelled without a reason.");
            }
            return true;
        }

        toggle.setValue(!toggle.getValue());
        NexusAPI.getApi().getPrimaryDatabase().saveSilent(toggle);
        String vc = MsgType.INFO.getVariableColor();
        String bc = MsgType.INFO.getBaseColor();
        player.sendMessage(MsgType.INFO + "Toggled " + vc + toggle.getInfo().getName() + bc + " to " + vc + toggle.getValue());
        return true;
    }
}
