package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.helper.StringHelper;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import org.bukkit.command.*;

import java.sql.*;
import java.util.*;

public class AltsCommand implements CommandExecutor {
    
    private NexusCore plugin;
    
    public AltsCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        if (senderRank.ordinal() > Rank.HELPER.ordinal()) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "You do not have permission to use that command."));
            return true;
        }
    
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Usage: /" + label + " <target>"));
            return true;
        }
    
        NexusPlayer target;
        try {
            UUID targetUUID = UUID.fromString(args[0]);
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(targetUUID);
            if (target == null) {
                target = NexusAPI.getApi().getDataManager().loadPlayer(targetUUID);
            }
        } catch (Exception e) {
            target = NexusAPI.getApi().getPlayerManager().getNexusPlayer(args[0]);
            if (target == null) {
                target = NexusAPI.getApi().getDataManager().loadPlayer(args[0]);
            }
        }
    
        if (target == null) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "Invalid target. Have they joined before?"));
            return true;
        }
    
        List<String> ips = new ArrayList<>();
        Set<IPEntry> ipHistory = NexusAPI.getApi().getPlayerManager().getIpHistory();
        for (IPEntry entry : ipHistory) {
            if (entry.getUuid().equals(target.getUniqueId())) {
                ips.add(entry.getIp()); //TODO Need to use the CachedPlayer.getIpHistory method
            }
        }
        
        if (ips.size() == 0) {
            sender.sendMessage(MCUtils.color(MsgType.WARN + "There is no IP History for that player, cannot find any alts."));
            return true;
        }
        
        Set<UUID> alts = new HashSet<>();
    
//        for (String ip : ips) {
//            Set<UUID> uuids = ipHistory.get(ip);
//            if (uuids != null && uuids.size() > 0) {
//                alts.addAll(uuids); //TODO recursive search for additional alts on additional ips
//            }
//        } //TODO
        
        Set<String> altNames = new HashSet<>();
        try (Connection connection = NexusAPI.getApi().getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select lastKnownName,uuid from players;");
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String name = resultSet.getString("lastKnownName");
                if (alts.contains(uuid)) {
                    altNames.add(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String altNameList = StringHelper.join(altNames, ", ");
        sender.sendMessage(MCUtils.color(MsgType.INFO + target.getName() + " has the following alt accounts..."));
        sender.sendMessage(MCUtils.color("&6&l> &b" + altNameList));
        return true;
    }
}
