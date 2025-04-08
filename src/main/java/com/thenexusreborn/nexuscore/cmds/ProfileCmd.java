package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.StarColors;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.time.TimeFormat;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class ProfileCmd extends NexusCommand<NexusCore> {
    
    public static final Set<Function<NexusPlayer, Map<String, Object>>> supplementals = new HashSet<>();
    
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
        TimeFormat timeFormat = new TimeFormat("%*#0y %%*#0mo %%*#0d %%*#0h %%#0m %%00s%");
        DecimalFormat numberFormat = new DecimalFormat("#,###,###,###.##");
        
        sender.sendMessage(StarColors.color("&6&l>> &eProfile information for &b" + target.getName()));
        sendProfileLine(sender, "Server", (target.getServer() != null ? target.getServer().getName() : "Not online"));
        sendProfileLine(sender, "Level", target.getExperience().getLevel());
        sendProfileLine(sender, "Level XP", numberFormat.format(target.getExperience().getLevelXp()));
        sendProfileLine(sender, "First Join", dateFormat.format(target.getPlayerTime().getFirstJoined()));
        sendProfileLine(sender, "Last Login", dateFormat.format(target.getPlayerTime().getLastLogin()));
        if (target.isOnline()) {
            sendProfileLine(sender, "Last Logout", "&aOnline");
        } else {
            sendProfileLine(sender, "Last Logout", dateFormat.format(target.getPlayerTime().getLastLogout()));
        }
        
        sendProfileLine(sender, "Play Time", timeFormat.format(target.getPlayerTime().getPlaytime()));
        sendProfileLine(sender, "Nexites", numberFormat.format(target.getBalance().getNexites()));
        sendProfileLine(sender, "Credits", numberFormat.format(target.getBalance().getCredits()));

        Rank rank = target.getEffectiveRank();
        String rankName = rank.getColor() + (rank.isBold() ? "&l" : "") + rank.name().replace("_", " ");
        sendProfileLine(sender, "Primary Rank", rankName);

        if (target.getNickname() == null) {
            StringBuilder secondaryRanksBuilder = new StringBuilder();
            Map<Rank, Long> ranks = target.getRanks().findAll();
            if (ranks.size() > 2) {
                ranks.forEach((r, expire) -> {
                    if (r == rank) {
                        return;
                    }
                    
                    String rn = r.getColor() + (r.isBold() ? "&l" : "") + r.name().replace("_", " ");
                    secondaryRanksBuilder.append(rn).append(" ");
                });
                sendProfileLine(sender, "Secondary Ranks", secondaryRanksBuilder.toString());
            }
        }
        
        if (target.hasActiveTag()) {
            sendProfileLine(sender, "Active Tag", target.getActiveTag().getDisplayName());
        } else {
            sendProfileLine(sender, "Active Tag", "Not Set");
        }
        
        StringBuilder tagsBuilder = new StringBuilder();
        for (String tag : target.getTags()) {
            tagsBuilder.append(new Tag(null, tag, 0L).getDisplayName()).append(" ");
        }
        
        sendProfileLine(sender, "Unlocked Tags", tagsBuilder.toString());

        PlayerToggles toggles = target.getToggles();

        for (Toggle.Info toggleInfo : NexusAPI.getApi().getToggleRegistry()) {
            if (target.getEffectiveRank().ordinal() <= toggleInfo.getMinRank().ordinal()) {
                sendProfileLine(sender, toggleInfo.getDisplayName(), toggles.getValue(toggleInfo.getName()));
            }
        }

        for (Function<NexusPlayer, Map<String, Object>> supplemental : supplementals) {
            Map<String, Object> values = supplemental.apply(target);
            values.forEach((k, v) -> sendProfileLine(sender, k, v));
        }

        return true;
    }
    
    private void sendProfileLine(CommandSender sender, String prefix, Object data) {
        sender.sendMessage(StarColors.color(" &6&l> &e" + prefix + ": &f" + data.toString()));
    }
}
