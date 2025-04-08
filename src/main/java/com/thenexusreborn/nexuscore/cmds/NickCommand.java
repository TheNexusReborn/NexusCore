package com.thenexusreborn.nexuscore.cmds;

import com.stardevllc.starcore.cmdflags.FlagResult;
import com.stardevllc.starcore.cmdflags.type.ComplexFlag;
import com.stardevllc.starcore.skins.Skin;
import com.stardevllc.starcore.skins.SkinManager;
import com.stardevllc.time.TimeParser;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.nickname.*;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.NexusCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class NickCommand extends NexusCommand<NexusCore> {
    
    private static final ComplexFlag RANK = new ComplexFlag("r", "Rank", Rank.MEMBER.toString());
    private static final ComplexFlag LEVEL = new ComplexFlag("l", "Level", 0);
    private static final ComplexFlag TARGET = new ComplexFlag("t", "target", null);
    private static final ComplexFlag SKIN = new ComplexFlag("s", "Skin", null);
    private static final ComplexFlag CREDITS = new ComplexFlag("c", "Credits", 0);
    private static final ComplexFlag NEXITES = new ComplexFlag("n", "Nexites", 0);
    private static final ComplexFlag PLAYTIME = new ComplexFlag("pt", "Play Time", 0);
    
    private static final List<Rank> ALLOWED_RANKS = List.of(Rank.MEMBER, Rank.IRON, Rank.GOLD, Rank.DIAMOND);
    
    public NickCommand(NexusCore plugin) {
        super(plugin, "nickname", "Set a nickname", Rank.MEDIA, "nick");
        this.playerOnly = true;
        
        this.cmdFlags.addFlag(RANK, LEVEL, TARGET, SKIN, CREDITS, NEXITES, PLAYTIME);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 0)) {
            MsgType.WARN.send(sender, "You must provide a name");
            return true;
        }
        
        String name = args[0];
        
        if (Bukkit.getPlayerExact(args[0]) == null) {
            plugin.getLogger().info("Could not find exact player");
            if (Bukkit.getPlayer(name) != null) {
                MsgType.WARN.send(sender, "You cannot use the name of a player already online");
                return true;
            }
        }

//        if (Bukkit.getPlayer(name) != null) {
//            MsgType.WARN.send(sender, "You cannot use the name of a player already online");
//            return true;
//        }
        
        UUID uuidFromName = NexusAPI.getApi().getPlayerManager().getUUIDFromName(name);
        if (uuidFromName != null) {
            Rank playerRank = NexusAPI.getApi().getPlayerManager().getPlayerRank(uuidFromName);
            if (playerRank.ordinal() <= Rank.MEDIA.ordinal()) {
                if (senderRank != Rank.NEXUS) {
                    MsgType.WARN.send(sender, "You cannot use the name of a player that holds a rank equal to or higher than %v.", Rank.MEDIA.getPrefix());
                    return true;
                }
            }
        }
        
        Rank rank = Rank.MEMBER;
        if (flagResults.getValue(RANK) != null && !flagResults.getValue(RANK).equals(Rank.MEMBER.toString())) {
            if (senderRank.ordinal() > Rank.MEDIA.ordinal()) {
                MsgType.WARN.send(sender, "You must have the rank %s or higher to set a custom rank.", Rank.MEDIA.getPrefix());
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
        
        int level = new Random().nextInt(6);
        if (flagResults.getValue(LEVEL) != null && !flagResults.getValue(LEVEL).equals("0")) {
            if (senderRank.ordinal() > Rank.VIP.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set a custom experience level.");
                return true;
            }
            
            try {
                level = Integer.parseInt(flagResults.getValue(LEVEL).toString());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid whole number.");
                return true;
            }
        }
        
        Player target = (Player) sender;
        if (flagResults.getValue(TARGET) != null) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
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
        
        SkinManager skinManager = Bukkit.getServer().getServicesManager().getRegistration(SkinManager.class).getProvider();
        Skin skin = null;
        if (flagResults.getValue(SKIN) != null) {
            if (senderRank.ordinal() > Rank.MVP.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set a custom skin.");
                return true;
            }
            
            skin = skinManager.getFromMojang(flagResults.getValue(SKIN).toString());
            if (skin == null) {
                MsgType.WARN.send(sender, "The name you provided for the skin is invalid.");
                return true;
            }
        }
        
        double credits = 0;
        if (flagResults.getValue(CREDITS) != null && !flagResults.getValue(CREDITS).equals("0")) {
            if (senderRank.ordinal() > Rank.MVP.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set custom credits.");
                return true;
            }
            
            try {
                credits = Integer.parseInt(flagResults.getValue(CREDITS).toString());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid whole number.");
                return true;
            }
        }
        
        double nexites = 0;
        if (flagResults.getValue(NEXITES) != null && !flagResults.getValue(NEXITES).equals("0")) {
            if (senderRank.ordinal() > Rank.MVP.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set custom nexites.");
                return true;
            }
            
            try {
                nexites = Integer.parseInt(flagResults.getValue(NEXITES).toString());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid whole number.");
                return true;
            }
        }
        
        long playtime = 0L;
        if (flagResults.getValue(PLAYTIME) != null && !flagResults.getValue(PLAYTIME).equals("0")) {
            if (senderRank.ordinal() > Rank.MVP.ordinal()) {
                MsgType.WARN.send(sender, "You are not allowed to set custom playtime.");
                return true;
            }
            
            TimeParser timeParser = new TimeParser();
            
            try {
                playtime = timeParser.parseTime(flagResults.getValue(PLAYTIME).toString());
            } catch (NumberFormatException e) {
                MsgType.WARN.send(sender, "You provided an invalid time value.");
                return true;
            }
        }
        
        if (skin == null) {
            skin = skinManager.getFromMojang(uuidFromName);
        }
        
        String skinIdentifier = skin != null ? skin.getIdentifier() : "";
        
        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(target.getUniqueId());
        Nickname nickname = new Nickname(target.getUniqueId(), name, target.getName(), skinIdentifier, rank);
        
        NickExperience nickExperience = new NickExperience(target.getUniqueId(), level, nexusPlayer.getTrueExperience());
        nickname.setFakeExperience(nickExperience);
        
        NickBalance nickBalance = new NickBalance(target.getUniqueId(), credits, nexites, nexusPlayer.getTrueBalance());
        nickname.setFakeBalance(nickBalance);
        
        NickTime nickTime = new NickTime(target.getUniqueId(), playtime, nexusPlayer.getTrueTime());
        nickname.setFakeTime(nickTime);
        
        nexusPlayer.setNickname(nickname);
        
        plugin.getNickWrapper().setNick(plugin, target, nickname.getName(), skin);
        
        if (target.getUniqueId().equals(((Player) sender).getUniqueId())) {
            MsgType.INFO.send(sender, "You successfully set your nick to %v.", nexusPlayer.getDisplayName());
        } else {
            MsgType.INFO.send(target, "Your nick was set to %v by %v.", nexusPlayer.getDisplayName(), sender.getName());
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            NexusAPI.getApi().getPrimaryDatabase().saveSilent(nexusPlayer);
        });
        
        return true;
    }
}
