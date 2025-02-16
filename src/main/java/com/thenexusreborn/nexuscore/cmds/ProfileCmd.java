package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.cmdflags.FlagResult;
import com.stardevllc.colors.StarColors;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ProfileCmd extends NexusCommand<NexusCore> {
    public ProfileCmd(NexusCore plugin) {
        super(plugin, "profile", "", Rank.HELPER);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        NexusPlayer target;
        
        if (sender instanceof ConsoleCommandSender) {
            if (!(args.length > 0)) {
                MsgType.WARN.send(sender, "You must provide a name to use that command.");
                return true;
            }
        }
        
        if (args.length > 0) {
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(args[0]);
            
            if (target == null) {
                MsgType.ERROR.send(sender, "Invalid player name %v", args[0]);
                return true;
            }
        } else {
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
        }
        
        if (target == null) {
            MsgType.ERROR.send(sender, "Invalid target");
            return true;
        }
        
        sender.sendMessage(StarColors.color("&6&l>> &eProfile information for &b" + target.getName()));
        sender.sendMessage(StarColors.color("&6&l > &eServer: &f" + (target.getServer() != null ? target.getServer().getName() : "Not online")));
        
        return true;
    }
}
