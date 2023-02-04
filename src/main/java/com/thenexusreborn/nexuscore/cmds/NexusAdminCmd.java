package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.helper.MojangHelper;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class NexusAdminCmd implements CommandExecutor {
    
    private NexusCore plugin;
    private NexusAPI nexusAPI;
    
    public NexusAdminCmd(NexusCore plugin) {
        this.plugin = plugin;
        this.nexusAPI = NexusAPI.getApi();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank rank = MCUtils.getSenderRank(plugin, sender);
        if (rank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You must provide a sub-command"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("privatealpha") || args[0].equalsIgnoreCase("pa")) {
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " " + args[0] + " <add|remove> <player>"));
                return true;
            }
            
            if (Stream.of("add", "a", "remove", "r").anyMatch(s -> args[1].equalsIgnoreCase(s))) {
                UUID uuid;
                String name;
                try {
                    uuid = UUID.fromString(args[2]);
                    CachedPlayer cachedPlayer = nexusAPI.getPlayerManager().getCachedPlayer(uuid);
                    if (cachedPlayer != null) {
                        name = cachedPlayer.getName();
                    } else {
                        name = MojangHelper.getNameFromUUID(uuid);
                    }
                } catch (Exception e) {
                    CachedPlayer cachedPlayer = nexusAPI.getPlayerManager().getCachedPlayer(args[2]);
                    if (cachedPlayer != null) {
                        uuid = cachedPlayer.getUniqueId();
                        name = cachedPlayer.getName();
                    } else {
                        uuid = MojangHelper.getUUIDFromName(args[2]);
                        name = args[2];
                    }
                }
                
                if (uuid == null) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "Could not find a player with that identifier."));
                    return true;
                }
                if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")) {
                    if (nexusAPI.getPrivateAlphaUsers().containsKey(uuid)) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "&e" + name + " &cis already on the private alpha list."));
                        return true;
                    }
                    PrivateAlphaUser pau = new PrivateAlphaUser(uuid, name, System.currentTimeMillis());
                    try {
                        nexusAPI.getPrimaryDatabase().save(pau);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (pau.getId() <= 0) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "There was a problem saving your changes to the database."));
                        return true;
                    }
                    nexusAPI.getPrivateAlphaUsers().put(pau.getUuid(), pau);
                    nexusAPI.getNetworkManager().send("updateprivatealpha", "add", pau.getId() + "", pau.getUuid().toString(), pau.getName(), pau.getTimestamp() + "");
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You added &b" + name + " &eto the private alpha list."));
                } else if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("r")) {
                    PrivateAlphaUser pau = nexusAPI.getPrivateAlphaUsers().get(uuid);
                    if (pau == null) {
                        sender.sendMessage(MCUtils.color(MsgType.WARN + "&e" + name + " &cis not on the private alpha list."));
                        return true;
                    }
                    
                    nexusAPI.getPrimaryDatabase().deleteSilent(PrivateAlphaUser.class, pau.getId());
                    nexusAPI.getNetworkManager().send("updateprivatealpha", "remove", pau.getUuid().toString());
                    sender.sendMessage(MCUtils.color(MsgType.INFO + "You removed &b" + name + " &efrom the private alpha list."));
                }
            } else if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("l")) {
                if (nexusAPI.getPrivateAlphaUsers().size() == 0) {
                    sender.sendMessage(MCUtils.color(MsgType.WARN + "There is no one directly on the private alpha list."));
                    return true;
                }
                
                List<String> names = new ArrayList<>();
                for (PrivateAlphaUser pau : new ArrayList<>(nexusAPI.getPrivateAlphaUsers().values())) {
                    names.add(pau.getName());
                }
                
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 0; i < names.size(); i++) {
                    nameBuilder.append("&a").append(names.get(i));
                    if (i < names.size() - 1) {
                        nameBuilder.append("&e, ");
                    }
                }
                
                sender.sendMessage(MCUtils.color("&6&l>> &ePrivate Alpha Users: &b" + nameBuilder));
            }
        }
        
        return true;
    }
}