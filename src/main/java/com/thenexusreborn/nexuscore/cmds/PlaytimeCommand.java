package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Session;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import me.firestar311.starlib.api.time.TimeFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeCommand implements CommandExecutor {
    
    private final NexusCore plugin;
    private final TimeFormat timeFormat = new TimeFormat("%*00y%%*00mo%%*00w%%*00d%%*00h%%*00m%%00s%");

    public PlaytimeCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        NexusPlayer nexusPlayer;
        boolean self = false;
        
        if (args.length == 0) {
            if (sender instanceof Player player) {
                nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
                self = true;
            } else {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a player name as the Console."));
                return true;
            }
        } else {
            nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(args[0]);
            if (nexusPlayer == null) {
                try {
                    nexusPlayer = NexusAPI.getApi().getPrimaryDatabase().get(NexusPlayer.class, "name", args[0]).get(0);
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not get a player by that name."));
                    e.printStackTrace();
                    return true;
                }
            }
        }

        long playtime = nexusPlayer.getStatValue("playtime").getAsLong();
        if (nexusPlayer.isOnline()) {
            Session session = nexusPlayer.getSession();
            if (session != null) {
                playtime += session.getTimeOnline();
            }
        }

        String formattedPlaytime = timeFormat.format(playtime);
        if (self) {
            sender.sendMessage(MCUtils.color(MsgType.INFO + "Your playtime is " + MsgType.INFO.getVariableColor() + formattedPlaytime));
        } else {
            sender.sendMessage(MCUtils.color(MsgType.INFO + nexusPlayer.getColoredName() + "&c's playtime is &b" + formattedPlaytime));
        }
        return true;
    }
}
