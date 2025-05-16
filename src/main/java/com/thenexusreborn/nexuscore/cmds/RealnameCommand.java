package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RealnameCommand extends NexusCommand<NexusCore> {
    public RealnameCommand(NexusCore plugin) {
        super(plugin, "realname", "Shows the real name of a player", Rank.HELPER);
        this.playerOnly = true;
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a target.");
            return true;
        }
        
        Player target = getPlayer(args[0]);
        if (target == null) {
            MsgType.WARN.send(sender, "Invalid target name %v.", args[0]);
            return true;
        }
        
        NexusPlayer targetNexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(target.getUniqueId());
        if (targetNexusPlayer == null) {
            MsgType.WARN.send(sender, "That player has no profile data loaded, please report to Firestar311");
            return true;
        }
        
        if (!targetNexusPlayer.isNicked() || targetNexusPlayer.getRank().ordinal() < senderRank.ordinal()) {
            MsgType.WARN.send(sender, "%v does not have a nickname.", targetNexusPlayer.getColoredName());
            return true;
        }
        
        MsgType.INFO.send(sender, "%v's realname is %v", targetNexusPlayer.getColoredName(), targetNexusPlayer.getTrueColoredName());
        return true;
    }
    
    protected Player getPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        
        return null;
    }
}
