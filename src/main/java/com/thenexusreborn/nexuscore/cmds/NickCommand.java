package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.api.Skin;
import com.stardevllc.starcore.api.cmdflags.FlagResult;
import com.stardevllc.starcore.api.cmdflags.type.ComplexFlag;
import com.stardevllc.starcore.api.cmdflags.type.PresenceFlag;
import com.stardevllc.starcore.skins.SkinManager;
import com.stardevllc.time.TimeParser;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.nickname.*;
import com.thenexusreborn.api.nickname.player.*;
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
    
    public NickCommand(NexusCore plugin) {
        super(plugin, "nickname", "Set a nickname", Rank.DIAMOND, "nick");
        this.playerOnly = true;
        
        this.cmdFlags.addFlag(RANK, LEVEL, TARGET, SKIN, CREDITS, NEXITES, TIME, PERSIST);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        String name;
        
        NickPerms nickPerms = NexusReborn.getNickPerms();
        
        Random random = new Random();
        if (args.length == 0) {
            List<String> randomNames = new ArrayList<>(NexusReborn.getRandomNames());
            
            if (randomNames.isEmpty()) {
                if (senderRank.ordinal() <= nickPerms.getCustomName().ordinal()) {
                    MsgType.WARN.send(sender, "There are no random names on the list. Please use a custom name and/or contact an Admin or higher with suggestions.");
                } else {
                    MsgType.WARN.send(sender, "There are no random names on the list, and you are not allowed to use a custom name. Please contact an Admin or higher with suggestions.");
                }
                return true;
            }
            
            do {
                name = randomNames.get(random.nextInt(randomNames.size()));
            } while (Bukkit.getPlayer(name) != null);
        } else {
            name = args[0];
        }
        
        if (name.length() < 3 || name.length() > 16) {
            MsgType.WARN.send(sender, "The name must be %v characters in length.", "3 - 16");
            return true;
        }
        
        Player target = (Player) sender;
        boolean self = false;
        if (!(name.equalsIgnoreCase("self") || name.equalsIgnoreCase(target.getName()))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    NexusPlayer np = NexusReborn.getPlayerManager().getNexusPlayer(p.getUniqueId());
                    if (np.getNickname() != null && np.getNickname().getName().equalsIgnoreCase(name)) {
                        target = p;
                        break;
                    }
                    
                    MsgType.WARN.send(sender, "You cannot use the name of a player already online");
                    return true;
                }
            }
            
            if (Bukkit.getPlayerExact(name) == null) {
                if (Bukkit.getPlayer(name) != null) {
                    MsgType.WARN.send(sender, "You cannot use the name of a player already online");
                    return true;
                }
            }
            
            if (NexusReborn.getNicknameBlacklist().contains(name.toLowerCase())) {
                if (senderRank.ordinal() > Rank.NEXUS.ordinal()) {
                    MsgType.WARN.send(sender, "That name is blacklisted from use. Please choose a different name.");
                    return true;
                }
            }
        } else {
            if (senderRank.ordinal() > nickPerms.getSelfTarget().ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to target yourself.");
                return true;
            }
            
            name = target.getName();
            self = true;
        }
        
        debug(sender, "Self: " + self);
        
        UUID uuidFromName = NexusReborn.getPlayerManager().getUUIDFromName(name);
        debug(sender, "UUID From Name: " + uuidFromName);
        if (uuidFromName != null) {
            Rank playerRank = NexusReborn.getPlayerManager().getPlayerRank(uuidFromName);
            debug(sender, "PlayerRank: " + playerRank);
            if (playerRank.ordinal() <= Rank.MEDIA.ordinal() && !self) {
                if (senderRank != Rank.NEXUS) {
                    MsgType.WARN.send(sender, "You cannot use the name of a player that holds a rank equal to or higher than %v.", Rank.MEDIA.getPrefix());
                    return true;
                }
            }
        }
        
        Rank rank = Rank.MEMBER;
        if (flagResults.getValue(RANK) != null && !flagResults.getValue(RANK).equals(Rank.MEMBER.toString())) {
            if (senderRank.ordinal() > nickPerms.getCustomRank().ordinal()) {
                MsgType.WARN.send(sender, "You must have the rank %s or higher to set a custom rank.", nickPerms.getCustomRank().getPrefix());
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
            if (senderRank.ordinal() > nickPerms.getSetOther().ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set the nickname of another player.");
                return true;
            }
            
            target = Bukkit.getPlayer(flagResults.getValue(TARGET).toString());
            
            if (target == null) {
                MsgType.WARN.send(sender, "Sorry, but the player %v is not online.", flagResults.getValue(TARGET));
                return true;
            }
            
            NexusPlayer targetNexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(target.getUniqueId());
            if (targetNexusPlayer.getRank().ordinal() <= senderRank.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set the nickname of someone that has a rank greater than or equal to your own.");
                return true;
            }
        }
        
        NexusPlayer nexusPlayer = NexusReborn.getPlayerManager().getNexusPlayer(target.getUniqueId());

        NickExperience nickExperience = null;
        if (flagResults.getValue(LEVEL) != null && !flagResults.getValue(LEVEL).equals("0")) {
            if (senderRank.ordinal() > nickPerms.getCustomLevel().ordinal()) {
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
            if (senderRank.ordinal() > nickPerms.getCustomSkin().ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set a custom skin.");
                return true;
            }
            
            skin = skinManager.getFromMojang(flagResults.getValue(SKIN).toString());
            if (skin == null) {
                MsgType.WARN.send(sender, "The name you provided for the skin is invalid.");
                return true;
            }
        }
        
        if (skin == null && !self) {
            if (uuidFromName != null) {
                skin = skinManager.getFromMojang(uuidFromName);
            } else {
                List<String> randomSkins = new ArrayList<>(NexusReborn.getRandomSkins());
                if (!randomSkins.isEmpty()) {
                    String skinRaw = randomSkins.get(random.nextInt(randomSkins.size()));
                    skin = skinManager.getFromMojang(skinRaw);
                }
            }
        }
        
        NickBalance creditsBalance = null;
        if (flagResults.getValue(CREDITS) != null && !flagResults.getValue(CREDITS).equals("0")) {
            if (senderRank.ordinal() > nickPerms.getCustomCredits().ordinal()) {
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
            if (senderRank.ordinal() > nickPerms.getCustomSkin().ordinal()) {
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
            if (senderRank.ordinal() > nickPerms.getCustomTime().ordinal()) {
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
            if (senderRank.ordinal() > nickPerms.getPersistStats().ordinal()) {
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
        
        if (!self) {
            plugin.getNickWrapper().setNick(plugin, target, nickname.getName(), skin);
        }
        
        if (target.getUniqueId().equals(((Player) sender).getUniqueId())) {
            MsgType.INFO.send(sender, "You successfully set your nick to %v.", nexusPlayer.getDisplayName());
        } else {
            MsgType.INFO.send(target, "Your nick was set to %v by %v.", nexusPlayer.getDisplayName(), sender.getName());
        }
        
        try {
            NicknameSetEvent nicknameSetEvent = new NicknameSetEvent(nexusPlayer, nickname);
            Bukkit.getPluginManager().callEvent(nicknameSetEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> NexusReborn.getPrimaryDatabase().saveSilent(nexusPlayer));
        
        return true;
    }
}
