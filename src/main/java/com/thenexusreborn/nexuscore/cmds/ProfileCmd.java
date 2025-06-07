package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.api.StarColors;
import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.stardevllc.time.TimeFormat;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tags.Tag;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
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
            Player t = getPlayer(args[0]);
            if (t == null) {
                MsgType.WARN.send(sender, "Invalid player name %v. Are they offline?", args[0]);
                return true;
            }
            
            target = NexusReborn.getPlayerManager().getNexusPlayer(t.getUniqueId());
            if (target == null) {
                MsgType.WARN.send(sender, "That player has no profile data loaded, please report to Firestar311");
                return true;
            }
            
            if (target != null && target.getNickname() != null && args[0].equalsIgnoreCase(target.getTrueName()) && target.getRank().ordinal() < senderRank.ordinal()) {
                target = null;
            }
            
            if (target == null) {
                MsgType.WARN.send(sender, "Invalid player name %v. Are they offline?", args[0]);
                return true;
            }
        } else {
            target = NexusReborn.getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId());
        }
        
        if (target == null) {
            MsgType.WARN.send(sender, "Invalid target. Are they offline?");
            return true;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
        TimeFormat timeFormat = new TimeFormat("%*#0y %%*#0mo %%*#0d %%*#0h %%#0m %%00s%");
        DecimalFormat numberFormat = new DecimalFormat("#,###,###,###.##");
        
        Rank rank = target.getEffectiveRank();
        
        boolean senderEqualOrHigher = senderRank.ordinal() <= target.getRank().ordinal();
        
        sender.sendMessage(StarColors.color("&6&l>> &eProfile information for &b" + target.getName()));
            sendProfileLine(sender, "Server", target.getServer() != null ? target.getServer().getName() : "Not online");
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
        sendProfileLine(sender, "Next Playtime Reward: ", timeFormat.format(target.getNextPlaytimeReward()));
        sendProfileLine(sender, "Nexites", numberFormat.format(target.getBalance().getNexites()));
        sendProfileLine(sender, "Credits", numberFormat.format(target.getBalance().getCredits()));
        
        String rankName = rank.getColor() + (rank.isBold() ? "&l" : "") + rank.name().replace("_", " ");
        sendProfileLine(sender, "Primary Rank", rankName);
        
        if (target.getNickname() != null && senderEqualOrHigher) {
            sendProfileLine(sender, "Real Primary Rank", target.getRank());
            sendProfileLine(sender, "Real Name", target.getTrueName());
        }
        
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
        
        for (Toggle.Info toggleInfo : NexusReborn.getToggleRegistry()) {
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
    
    protected Player getPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        
        return null;
    }
    
    private void sendProfileLine(CommandSender sender, String prefix, Object data) {
        sender.sendMessage(StarColors.color(" &6&l> &e" + prefix + ": &f" + data.toString()));
    }
}
