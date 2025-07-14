package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.stardevllc.starmclib.skin.SkinAPI;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.nickname.Nickname;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.api.events.NicknameRemoveEvent;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnnickCommand extends NexusCommand<NexusCore> {
    public UnnickCommand(NexusCore plugin) {
        super(plugin, "unnickname", "Removes your nickname", Rank.MEMBER, "unnick");
        this.playerOnly = true;
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        NexusPlayer nexusPlayer;
        
        boolean self = false;
        if (args.length > 0) {
            nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(args[0]);
            
            if (nexusPlayer == null) {
                MsgType.WARN.send(sender, "Invalid target %v", args[0]);
                return true;
            }
            
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                MsgType.WARN.send(sender, "Only Admin+ can remove a nickname from others.");
                return true;
            }
        } else {
            nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
            self = true;
        }
        
        if (!nexusPlayer.isNicked() || nexusPlayer.getRank().ordinal() < senderRank.ordinal()) {
            if (self) {
                MsgType.WARN.send(sender, "You do not have a nickname set.");
            } else {
                MsgType.WARN.send(sender, nexusPlayer.getColoredName() + " does not have a nickname set.");
            }
            return true;
        }
        
        Nickname nickname = nexusPlayer.getNickname();
        
        plugin.getNickWrapper().setNick(plugin, (Player) sender, nexusPlayer.getTrueName(), SkinAPI.getFromMojang(nexusPlayer.getUniqueId()));
        if (nickname.isPersist()) {
            nickname.setActive(false);
        } else {
            nexusPlayer.setNickname(null);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> NexusReborn.getPrimaryDatabase().saveSilent(nexusPlayer));
        if (self) {
            MsgType.INFO.send(sender, "You successfully unnicked yourself.");
        } else {
            MsgType.INFO.send(sender, "You successfully unnicked " + nexusPlayer.getColoredName());
        }
        
        try {
            NicknameRemoveEvent nicknameRemoveEvent = new NicknameRemoveEvent(nexusPlayer, nickname);
            Bukkit.getPluginManager().callEvent(nicknameRemoveEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
}
