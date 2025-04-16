package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.time.TimeFormat;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.api.sql.objects.codecs.RanksCodec;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaytimeCommand extends NexusCommand<NexusCore> {

    private final TimeFormat timeFormat = new TimeFormat("%*00y%%*00mo%%*00w%%*00d%%*00h%%*00m%%00s%");

    public PlaytimeCommand(NexusCore plugin) {
        super(plugin, "playtime", "", Rank.MEMBER);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        UUID uuid;
        boolean self;

        if (args.length == 0) {
            if (sender instanceof Player player) {
                uuid = player.getUniqueId();
                self = true;
            } else {
                sender.sendMessage(StarColors.color(MsgType.WARN + "You must provide a player name as the Console."));
                return true;
            }
        } else {
            self = false;
            try {
                uuid = UUID.fromString(args[0]);
            } catch (IllegalArgumentException e) {
                uuid = NexusAPI.getApi().getPlayerManager().getUUIDFromName(args[0]);
            }

            if (uuid == null) {
                sender.sendMessage(StarColors.color(MsgType.WARN + "Could not get a player by that identifier."));
                return true;
            }
        }

        NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
        if (player != null) {
            long playtime = player.getPlayerTime().getPlaytime();
            if (player.isOnline()) {
                Session session = player.getSession();
                if (session != null) {
                    playtime += session.getTimeOnline();
                }
            }

            sendPlaytimeMessages(playtime, self, player, player.getColoredName());
        } else {
            UUID finalUuid = uuid;
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                String name = NexusAPI.getApi().getPlayerManager().getNameFromUUID(finalUuid);
                SQLDatabase database = NexusAPI.getApi().getPrimaryDatabase();
                try {
                    PlayerRanks ranks = new RanksCodec().decode(database.executeQuery("select `ranks` from `players` where `uniqueId`='" + finalUuid + "';").getFirst().getString("ranks"));
                    PlayerTime playerTime = database.get(PlayerTime.class, "uniqueid", player.getUniqueId().toString()).getFirst();
                    sendPlaytimeMessages(playerTime.getPlaytime(), self, player, ranks.get().getColor() + name);
                } catch (Exception e) {
                    sender.sendMessage(StarColors.color(MsgType.ERROR + "There was an error getting " + name + "'s playtime."));
                }
            });
        }

        return true;
    }
    
    private void sendPlaytimeMessages(long playtime, boolean self, NexusPlayer sender, String coloredName) {
        String formattedPlaytime = timeFormat.format(playtime);
        if (self) {
            String line = MsgType.INFO + "Your playtime is " + MsgType.INFO.getVariableColor() + formattedPlaytime;
            if (sender.isNicked()) {
                line += " &8(&7" + timeFormat.format(sender.getTrueTime().getPlaytime()) + "&8)";
            }
            
            sender.sendMessage(StarColors.color(line));
        } else {
            sender.sendMessage(StarColors.color(MsgType.INFO + coloredName + "&e's playtime is &b" + formattedPlaytime));
        }
    }
}
