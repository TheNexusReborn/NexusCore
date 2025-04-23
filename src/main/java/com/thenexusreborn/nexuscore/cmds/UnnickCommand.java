package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.starcore.skins.SkinManager;
import com.thenexusreborn.api.NexusAPI;
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
        super(plugin, "unnickname", "Removes your nickname", Rank.DIAMOND, "unnick");
        this.playerOnly = true;
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
        
        if (!nexusPlayer.isNicked()) {
            MsgType.WARN.send(sender, "You do not have a nickname set.");
            return true;
        }
        
        Nickname nickname = nexusPlayer.getNickname();
        
        SkinManager skinManager = Bukkit.getServicesManager().getRegistration(SkinManager.class).getProvider();
        plugin.getNickWrapper().setNick(plugin, ((Player) sender), nexusPlayer.getTrueName(), skinManager.getFromMojang(nexusPlayer.getUniqueId()));
        nickname.setActive(false);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> NexusAPI.getApi().getPrimaryDatabase().saveSilent(nexusPlayer));
        MsgType.INFO.send(sender, "You successfully unnicked yourself.");
        
        try {
            NicknameRemoveEvent nicknameRemoveEvent = new NicknameRemoveEvent(nexusPlayer, nickname);
            Bukkit.getPluginManager().callEvent(nicknameRemoveEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
}
