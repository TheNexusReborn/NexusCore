package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.PlayerRanks;
import com.thenexusreborn.api.player.Session;
import com.thenexusreborn.api.stats.Stat;
import com.thenexusreborn.api.storage.codec.RanksCodec;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import me.firestar311.starlib.api.time.TimeFormat;
import me.firestar311.starsql.api.objects.SQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaytimeCommand implements CommandExecutor {

    private final NexusCore plugin;
    private final TimeFormat timeFormat = new TimeFormat("%*00y%%*00mo%%*00w%%*00d%%*00h%%*00m%%00s%");

    public PlaytimeCommand(NexusCore plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        UUID uuid;
        boolean self;

        if (args.length == 0) {
            if (sender instanceof Player player) {
                uuid = player.getUniqueId();
                self = true;
            } else {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a player name as the Console."));
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
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not get a player by that identifier."));
                return true;
            }
        }

        NexusPlayer player = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
        if (player != null) {
            long playtime = player.getStatValue("playtime").getAsLong();
            if (player.isOnline()) {
                Session session = player.getSession();
                if (session != null) {
                    playtime += session.getTimeOnline();
                }
            }

           sendPlaytimeMessages(playtime, self, sender, player.getColoredName());
        } else {
            UUID finalUuid = uuid;
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                String name = NexusAPI.getApi().getPlayerManager().getNameFromUUID(finalUuid);
                SQLDatabase database = NexusAPI.getApi().getPrimaryDatabase();
                try {
                    long playtime = database.get(Stat.class, new String[]{"uuid", "name"}, new String[]{finalUuid.toString(), "playtime"}).get(0).getValue().getAsLong();
                    PlayerRanks ranks = new RanksCodec().decode(database.executeQuery("select `ranks` from `players` where `uniqueId`='" + finalUuid + "';").get(0).getString("ranks"));
                    sendPlaytimeMessages(playtime, self, sender, ranks.get().getColor() + name);
                } catch (Exception e) {
                    sender.sendMessage(MCUtils.color(MsgType.ERROR + "There was an error getting " + name + "'s playtime."));
                }
            });
        }
        
        return true;
    }
    
    private void sendPlaytimeMessages(long playtime, boolean self, CommandSender sender, String coloredName) {
        String formattedPlaytime = timeFormat.format(playtime);
        if (self) {
            sender.sendMessage(MCUtils.color(MsgType.INFO + "Your playtime is " + MsgType.INFO.getVariableColor() + formattedPlaytime));
        } else {
            sender.sendMessage(MCUtils.color(MsgType.INFO + coloredName + "&e's playtime is &b" + formattedPlaytime));
        }
    }
}
