package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.starcore.cmdflags.type.ComplexFlag;
import com.stardevllc.starcore.cmdflags.type.PresenceFlag;
import com.stardevllc.starcore.skins.Skin;
import com.stardevllc.starcore.skins.SkinManager;
import com.stardevllc.time.TimeParser;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.nickname.*;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.api.events.NicknameSetEvent;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class NickCommand extends NexusCommand<NexusCore> {
    
    private static final ComplexFlag RANK = new ComplexFlag("r", "Rank", null);
    private static final ComplexFlag LEVEL = new ComplexFlag("l", "Level", null);
    private static final ComplexFlag TARGET = new ComplexFlag("t", "target", null);
    private static final ComplexFlag SKIN = new ComplexFlag("s", "Skin", null);
    private static final ComplexFlag CREDITS = new ComplexFlag("c", "Credits", null);
    private static final ComplexFlag NEXITES = new ComplexFlag("n", "Nexites", null);
    private static final ComplexFlag TIME = new ComplexFlag("t", "Time", null);
    private static final PresenceFlag PERSIST = new PresenceFlag("p", "Persist");
    
    private static final List<Rank> ALLOWED_RANKS = List.of(Rank.MEMBER, Rank.IRON, Rank.GOLD, Rank.DIAMOND);
    
    private static final Rank SELF_TARGET = Rank.DIAMOND;
    private static final Rank CUSTOM_RANK = Rank.MEDIA;
    private static final Rank SET_OTHER = Rank.ADMIN;
    private static final Rank CUSTOM_LEVEL = Rank.VIP;
    private static final Rank CUSTOM_SKIN = Rank.VIP;
    private static final Rank CUSTOM_CREDITS = Rank.VIP;
    private static final Rank CUSTOM_NEXITES = Rank.VIP;
    private static final Rank CUSTOM_TIME = Rank.VIP;
    private static final Rank PERSIST_STATS = Rank.VIP;
    
    public NickCommand(NexusCore plugin) {
        super(plugin, "nickname", "Set a nickname", Rank.MEDIA, "nick");
        this.playerOnly = true;
        
        this.cmdFlags.addFlag(RANK, LEVEL, TARGET, SKIN, CREDITS, NEXITES, TIME, PERSIST);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a name");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("addblacklist")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                MsgType.WARN.send(sender, "That name is blacklisted, you cannot use it.");
                return true;
            }
            
            if (!(args.length > 1)) {
                MsgType.WARN.send(sender, "You must provide one or more names to add to the blacklist.");
                return true;
            }
            
            Set<String> nicknameBlacklist = NexusAPI.getApi().getNicknameBlacklist();
            
            for (int i = 1; i < args.length; i++) {
                String name = args[i].toLowerCase();
                if (!nicknameBlacklist.contains(name)) {
                    nicknameBlacklist.add(name);
                    MsgType.INFO.send(sender, "You added %v to the blacklist.", name);
                } else {
                    MsgType.WARN.send(sender, "That name is already on the blacklist");
                    return true;
                }
            }
            
            return true;
        } else if (args[0].equalsIgnoreCase("removeblacklist")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                MsgType.WARN.send(sender, "That name is blacklisted, you cannot use it.");
                return true;
            }
            
            if (!(args.length > 1)) {
                MsgType.WARN.send(sender, "You must provide one or more names to remove from the blacklist.");
                return true;
            }
            
            Set<String> nicknameBlacklist = NexusAPI.getApi().getNicknameBlacklist();
            
            for (int i = 1; i < args.length; i++) {
                String name = args[i].toLowerCase();
                if (nicknameBlacklist.contains(name)) {
                    nicknameBlacklist.remove(name);
                    MsgType.INFO.send(sender, "You removed %v from the blacklist.", name);
                } else {
                    MsgType.WARN.send(sender, "That name is not on the blacklist");
                    return true;
                }
            }
            
            return true;
        }
        
        String name = args[0];
        
        Player target = (Player) sender;
        boolean self = false;
        if (!(name.equalsIgnoreCase("self") || name.equalsIgnoreCase(target.getName()))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    NexusPlayer np = NexusAPI.getApi().getPlayerManager().getNexusPlayer(p.getUniqueId());
                    if (np.getNickname() != null && np.getNickname().getName().equalsIgnoreCase(name)) {
                        target = p;
                        break;
                    }
                    
                    MsgType.WARN.send(sender, "You cannot use the name of a player already online");
                    return true;
                }
            }
            
            if (Bukkit.getPlayerExact(args[0]) == null) {
                if (Bukkit.getPlayer(name) != null) {
                    MsgType.WARN.send(sender, "You cannot use the name of a player already online");
                    return true;
                }
            }
            
            if (NexusAPI.getApi().getNicknameBlacklist().contains(name.toLowerCase())) {
                if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                    MsgType.WARN.send(sender, "That name is blacklisted from use. Please choose a different name.");
                    return true;
                }
            }
        } else {
            if (senderRank.ordinal() > SELF_TARGET.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to target yourself.");
                return true;
            }
            
            name = target.getName();
            self = true;
        }
        
        UUID uuidFromName = NexusAPI.getApi().getPlayerManager().getUUIDFromName(name);
        if (uuidFromName != null) {
            Rank playerRank = NexusAPI.getApi().getPlayerManager().getPlayerRank(uuidFromName);
            if (playerRank.ordinal() <= Rank.MEDIA.ordinal() && !self) {
                if (senderRank != Rank.NEXUS) {
                    MsgType.WARN.send(sender, "You cannot use the name of a player that holds a rank equal to or higher than %v.", Rank.MEDIA.getPrefix());
                    return true;
                }
            }
        }
        
        Rank rank = Rank.MEMBER;
        if (flagResults.getValue(RANK) != null && !flagResults.getValue(RANK).equals(Rank.MEMBER.toString())) {
            if (senderRank.ordinal() > CUSTOM_RANK.ordinal()) {
                MsgType.WARN.send(sender, "You must have the rank %s or higher to set a custom rank.", CUSTOM_RANK.getPrefix());
                return true;
            }
            
            try {
                rank = Rank.valueOf(flagResults.getValue(RANK).toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                MsgType.WARN.send(sender, "Invalid rank name provided: %v.", flagResults.getValue(RANK));
                return true;
            }
            
            if (!ALLOWED_RANKS.contains(rank)) {
                if (senderRank != Rank.NEXUS) {
                    MsgType.WARN.send(sender, "You cannot use that rank.");
                    return true;
                }
            }
        }
        
        if (flagResults.getValue(TARGET) != null) {
            if (senderRank.ordinal() > SET_OTHER.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set the nickname of another player.");
                return true;
            }
            
            target = Bukkit.getPlayer(flagResults.getValue(TARGET).toString());
            
            if (target == null) {
                MsgType.WARN.send(sender, "Sorry, but the player %v is not online.", flagResults.getValue(TARGET));
                return true;
            }
            
            NexusPlayer targetNexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(target.getUniqueId());
            if (targetNexusPlayer.getRank().ordinal() <= senderRank.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set the nickname of someone that has a rank greater than or equal to your own.");
                return true;
            }
        }
        
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(target.getUniqueId());

        NickExperience nickExperience = null;
        if (flagResults.getValue(LEVEL) != null && !flagResults.getValue(LEVEL).equals("0")) {
            if (senderRank.ordinal() > CUSTOM_LEVEL.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set a custom experience level.");
                return true;
            }
            
            try {
                int level = Integer.parseInt(flagResults.getValue(LEVEL).toString());
                nickExperience = new NickExperience(target.getUniqueId(), level, nexusPlayer.getTrueExperience());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid whole number.");
                return true;
            }
        }
        
        SkinManager skinManager = Bukkit.getServer().getServicesManager().getRegistration(SkinManager.class).getProvider();
        Skin skin = null;
        if (flagResults.getValue(SKIN) != null) {
            if (senderRank.ordinal() > CUSTOM_SKIN.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set a custom skin.");
                return true;
            }
            
            skin = skinManager.getFromMojang(flagResults.getValue(SKIN).toString());
            if (skin == null) {
                MsgType.WARN.send(sender, "The name you provided for the skin is invalid.");
                return true;
            }
        }
        
        if (skin == null && uuidFromName != null) {
            skin = skinManager.getFromMojang(uuidFromName);
        }
        
        NickBalance creditsBalance = null;
        if (flagResults.getValue(CREDITS) != null && !flagResults.getValue(CREDITS).equals("0")) {
            if (senderRank.ordinal() > CUSTOM_CREDITS.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set custom credits.");
                return true;
            }
            
            try {
                double credits = Integer.parseInt(flagResults.getValue(CREDITS).toString());
                creditsBalance = new NickBalance(target.getUniqueId(), credits, 0, nexusPlayer.getTrueBalance());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid whole number.");
                return true;
            }
        }
        
        NickBalance nexitesBalance = null;
        if (flagResults.getValue(NEXITES) != null && !flagResults.getValue(NEXITES).equals("0")) {
            if (senderRank.ordinal() > CUSTOM_NEXITES.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set custom nexites.");
                return true;
            }
            
            try {
                double nexites = Integer.parseInt(flagResults.getValue(NEXITES).toString());
                nexitesBalance = new NickBalance(target.getUniqueId(), 0, nexites, nexusPlayer.getTrueBalance());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid whole number.");
                return true;
            }
        }
        
        NickTime nickTime = null;
        if (flagResults.getValue(TIME) != null && !flagResults.getValue(TIME).equals("0")) {
            if (senderRank.ordinal() > CUSTOM_TIME.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set custom playtime.");
                return true;
            }
            
            TimeParser timeParser = new TimeParser();
            
            try {
                long playtime = timeParser.parseTime(flagResults.getValue(TIME).toString());
                nickTime = new NickTime(target.getUniqueId(), playtime, nexusPlayer.getTrueTime());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid time value.");
                return true;
            }
        }
        
       
        boolean persist = false;
        if (flagResults.isPresent(PERSIST)) {
            if (senderRank.ordinal() > PERSIST_STATS.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to persist stats across nicknames.");
                return true;
            }
            
            persist = true;
        }
        
        String skinIdentifier = skin != null ? skin.getIdentifier() : "";
        
        Nickname nickname;
        if (nexusPlayer.getNickname() != null) {
            nickname = nexusPlayer.getNickname();
            nickname.setName(name);
            nickname.setSkin(skinIdentifier);
            nickname.setRank(rank);
        } else {
            nickname = new Nickname(target.getUniqueId(), name, target.getName(), skinIdentifier, rank);
            nexusPlayer.setNickname(nickname);
        }
        
        nickname.setPersist(persist);
        
        if (nickExperience != null) {
            nickname.setFakeExperience(nickExperience);
        }
        
        if (creditsBalance != null) {
            nickname.getFakeBalance().setCredits(creditsBalance.getCredits());
        }
        
        if (nexitesBalance != null) {
            nickname.getFakeBalance().setNexites(nexitesBalance.getNexites());
        }
        
        if (nickTime != null) {
            nickname.setFakeTime(nickTime);
        }
        
        nickname.getFakeBalance().setTrueBalance(nexusPlayer.getTrueBalance());
        nickname.getFakeExperience().setTrueExperience(nexusPlayer.getTrueExperience());
        nickname.getFakeTime().setTrueTime(nexusPlayer.getTrueTime());
        
        nickname.setActive(true);
        
        if (!(nickname.getName().equalsIgnoreCase(nickname.getTrueName()) && skin == null)) {
            plugin.getNickWrapper().setNick(plugin, target, nickname.getName(), skin);
        }
        
        if (target.getUniqueId().equals(((Player) sender).getUniqueId())) {
            MsgType.INFO.send(sender, "You successfully set your nick to %v.", nexusPlayer.getDisplayName());
        } else {
            MsgType.INFO.send(target, "Your nick was set to %v by %v.", nexusPlayer.getDisplayName(), sender.getName());
        }
        
        NicknameSetEvent nicknameSetEvent = new NicknameSetEvent(nexusPlayer, nickname);
        Bukkit.getPluginManager().callEvent(nicknameSetEvent);
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> NexusAPI.getApi().getPrimaryDatabase().saveSilent(nexusPlayer));
        
        return true;
    }
}
