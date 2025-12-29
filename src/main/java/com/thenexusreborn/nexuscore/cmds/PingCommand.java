package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starmclib.command.flags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends NexusCommand<NexusCore> {
    public PingCommand(NexusCore plugin) {
        super(plugin, "ping", "", Rank.MEMBER);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player;
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                MsgType.WARN.send(sender, "Console must provide a player name.");
                return true;
            }
        }
        
        if (args.length > 0 && senderRank.ordinal() <= Rank.HELPER.ordinal()) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                MsgType.WARN.send(sender, "Invalid player name %v", args[0]);
                return true;
            }
        } else {
            player = (Player) sender;
        }
        
        MsgType.INFO.send(sender, player.getName() + "'s ping is %v", ((CraftPlayer) player).getHandle().ping + "ms");
        return true;
    }
}
