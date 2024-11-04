package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.discord.DiscordVerifyCode;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;

public class VerifyCommand implements CommandExecutor {
    
    private NexusCore plugin;

    public VerifyCommand(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MsgType.WARN.format("Only players can use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            player.sendMessage(MsgType.WARN.format("Usage: /verify <link code>"));
            return true;
        }
        
        String linkCode = args[0];
        for (DiscordVerifyCode discordVerifyCode : new ArrayList<>(plugin.getDiscordVerifyCodes())) {
            if (discordVerifyCode.getCode().equals(linkCode)) {
                NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
                nexusPlayer.setDiscordId(discordVerifyCode.getDiscordId());
                try {
                    NexusAPI.getApi().getPrimaryDatabase().save(nexusPlayer);
                    plugin.getDiscordVerifyCodes().remove(discordVerifyCode);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        return true;
    }
}
