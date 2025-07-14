package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.discord.DiscordVerifyCode;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;

public class VerifyCommand extends NexusCommand<NexusCore> {

    public VerifyCommand(NexusCore plugin) {
        super(plugin, "verify", "", Rank.MEMBER);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
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
                NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(player.getUniqueId());
                nexusPlayer.setDiscordId(discordVerifyCode.getDiscordId());
                try {
                    NexusReborn.getPrimaryDatabase().save(nexusPlayer);
                    plugin.getDiscordVerifyCodes().remove(discordVerifyCode);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }
}
