package com.thenexusreborn.api.util;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;

import java.util.ArrayList;
import java.util.UUID;

public final class StaffChat {
    /*
    The base format for the data or args for the staff chat network command is as follows
    
    staffchat <origin> <event> <data...>
    <origin> is the origin of the message, which is handled automatically by the networking api
    <event> is what happened, which is used for formatting
    <data..> is an array of the relevent data for the action
    
    Valid actions are: Chat, join, disconnect, toggle (incognito, vanish, staffmode), nickname (set, reset), punishment, report, anticheat
    Chat: staffchat <origin> chat <player> <message...>
    Join: staffchat <origin> join <player> - The server that is displayed is the origin
    Disconnect: staffchat <origin> disconnect <player>
    Anticheat: staffchat <origin> anticheat <player> <hack> <violation>
    Toggle: Base features not implemented
    Nickname: Base feature not implemented
    Punishment: staffchat <origin> punishment <id>
    Report: Base feature not implemented
     */
    
    public static final String PREFIX = "&2&l[&aSTAFF&2&l]";
    
    public static void handleIncoming(String origin, String[] args) {
        String event = args[0];
        String format = "";
        String displayName = "";
        Rank rank = Rank.valueOf(args[2]);
        try {
            UUID uuid = UUID.fromString(args[1]);
            String name = NexusAPI.getApi().getPlayerManager().getNameFromUUID(uuid);
    
            displayName = rank.getColor() + name;
            if (event.equals("anticheat")) {
                String hack = args[2];
                int violation = Integer.parseInt(args[3]);
                format = "{prefix} &8[&9PMR&8] &8[&6{origin}&8] &r{displayName} &7is using &e" + hack + " &bVL:" + violation;
            }
        } catch (Exception e) {
            
        }
        
        format = format.replace("{prefix}", PREFIX).replace("{displayName}", displayName).replace("{origin}", origin);
    
        for (NexusPlayer player : new ArrayList<>(NexusAPI.getApi().getPlayerManager().getPlayers().values())) {
            if (player.isOnline()) {
                if (player.getRank().ordinal() <= Rank.HELPER.ordinal()) {
                    player.sendMessage(format);
                }
            }
        }
    }
}
