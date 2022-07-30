package com.thenexusreborn.nexuscore.cmds;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.helper.StringHelper;
import com.thenexusreborn.api.player.*;
import com.thenexusreborn.api.tournament.Tournament;
import com.thenexusreborn.api.tournament.Tournament.ScoreInfo;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.nexuscore.util.timer.Timer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TournamentCommand implements TabExecutor {
    
    private final NexusCore plugin;
    
    public TournamentCommand(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(plugin, sender);
        
        if (!(args.length > 0)) {
            sender.sendMessage(MCUtils.color("&cYou must provide a sub command"));
            return true;
        }
        
        Tournament tournament = NexusAPI.getApi().getTournament();
        
        if (args[0].equalsIgnoreCase("create")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
            
            if (tournament != null) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "There is a tournament already created, please delete before creating another."));
                return true;
            }
            
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color("&cYou must provide a host."));
                return true;
            }
            
            NexusPlayer host = MCUtils.getPlayerFromInput(args[1]);
            
            if (host == null) {
                sender.sendMessage(MCUtils.color("&cYou did not provide a valid host."));
                return true;
            }
            
            if (!(args.length > 2)) {
                sender.sendMessage(MCUtils.color("&cYou must provide a name for the tournament"));
                return true;
            }
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                nameBuilder.append(args[i]).append(" ");
            }
            
            tournament = new Tournament(host.getUniqueId(), nameBuilder.substring(0, nameBuilder.length() - 1));
            NexusAPI.getApi().getDataManager().pushTournament(tournament);
            if (tournament.getId() == 0) {
                sender.sendMessage(MCUtils.color(MsgType.WARN + "Error while creating a tournament."));
                return true;
            }
            NexusAPI.getApi().setTournament(tournament);
            NexusAPI.getApi().getNetworkManager().send("tournament ", tournament.getId() + "");
            sender.sendMessage(MCUtils.color(MsgType.INFO + "Successfully created a tournament with the name &b" + tournament.getName() + " &eand id &b" + tournament.getId()));
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
            
            if (tournament == null) {
                sender.sendMessage(MCUtils.color("&cThere is no tournament set up right now."));
                return true;
            }
            
            NexusAPI.getApi().setTournament(null);
            
            sender.sendMessage(MCUtils.color(MsgType.VERBOSE + "Removing existing stats from that tournament..."));
            //TODO
//            Tournament finalTournament1 = tournament;
//            NexusAPI.getApi().getThreadFactory().runAsync(() -> {
//                try (Connection connection = NexusAPI.getApi().getConnection(); Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
//                    ResultSet resultSet = statement.executeQuery("select * from stats where name like concat('%', 'tournament', '%');");
//                    while (resultSet.next()) {
//                        resultSet.deleteRow();
//                    }
//                    
//                    resultSet = statement.executeQuery("select * from statchanges where statName like concat('%', 'tournament', '%');");
//                    while (resultSet.next()) {
//                        resultSet.deleteRow();
//                    }
//                    
//                    statement.execute("delete from tournaments where id='" + finalTournament1.getId() + "';");
//                    NexusAPI.getApi().getNetworkManager().send("tournament", "delete", finalTournament1.getId() + "");
//                } catch (Exception e) {
//                    sender.sendMessage(MCUtils.color(MsgType.WARN + "There was an error processing stat purge"));
//                    e.printStackTrace();
//                }
//            });
            
            sender.sendMessage(MCUtils.color("&eYou successfully deleted the tournament"));
            return true;
        } else {
            if (tournament == null) {
                sender.sendMessage(MCUtils.color("&cThere is no tournament currently setup."));
                return true;
            }
            
            if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb")) {
                Tournament finalTournament = tournament;
                sender.sendMessage(MCUtils.color("&7&oPlease wait, generating leaderboard..."));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        long lastUpdated = 0;
                        SortedSet<ScoreInfo> leaderboard = new TreeSet<>(NexusAPI.getApi().getTournament().getScoreCache().values());
                        List<String> lines = new LinkedList<>();
                        int place = 1;
                        for (ScoreInfo scoreInfo : leaderboard) {
                            if (scoreInfo.getLastUpdated() > lastUpdated) {
                                lastUpdated = scoreInfo.getLastUpdated();
                            }
                            lines.add("  &a" + place + ". &b" + scoreInfo.getName() + " &7-> &f" + scoreInfo.getScore());
                            place++;
                        }
    
                        String formattedLastUpdated;
                        if (lastUpdated == 0) {
                            formattedLastUpdated = "Never";
                        } else {
                            formattedLastUpdated = Timer.formatLongerTime((int) ((System.currentTimeMillis() - lastUpdated)) / 1000) + " ago";
                        }
                        
                        sender.sendMessage(MCUtils.color("&6&l>> &eLeaderboard for tournament &b" + finalTournament.getName()));
                        sender.sendMessage(MCUtils.color("&6&l>> &7&oLast Updated: " + formattedLastUpdated));
                        lines.forEach(line -> sender.sendMessage(MCUtils.color(line)));
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            } else if (args[0].equalsIgnoreCase("score")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MCUtils.color("&cOnly players can use that command."));
                    return true;
                }
                
                Player player = (Player) sender;
    
                ScoreInfo scoreInfo = NexusAPI.getApi().getTournament().getScoreCache().getOrDefault(player.getUniqueId(), new ScoreInfo(player.getUniqueId(), player.getName(), 0));
                int score = scoreInfo.getScore();
                player.sendMessage(MCUtils.color("&eYour score is currently &b" + score));
                String lastUpdated;
                if (scoreInfo.getLastUpdated() == 0) {
                    lastUpdated = "Never";
                } else {
                    lastUpdated = Timer.formatLongerTime((int) ((System.currentTimeMillis() - scoreInfo.getLastUpdated())) / 1000) + " ago";
                }
                sender.sendMessage(MCUtils.color("&6&l>> &7&oLast Updated: " + lastUpdated));
                return true;
            }
            
            boolean isHost, isAdmin;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                isHost = player.getUniqueId().equals(tournament.getHost());
                isAdmin = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId()).getRank().ordinal() <= Rank.ADMIN.ordinal();
            } else {
                isHost = false;
                isAdmin = true;
            }
            
            if (!(isHost || isAdmin)) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to do that."));
                return true;
            }
            
            if (!(args.length > 1)) {
                sender.sendMessage(MCUtils.color("&cNot enough arguments."));
                return true;
            }
            
            if (args[0].equalsIgnoreCase("setactive") || args[0].equalsIgnoreCase("sa")) {
                boolean value = Boolean.parseBoolean(args[1]);
                tournament.setActive(value);
                sender.sendMessage(MCUtils.color("&eYou set the tournament active status to &b" + value));
            } else if (args[0].equalsIgnoreCase("setname") || args[0].equalsIgnoreCase("sn")) {
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    nameBuilder.append(args[i]).append(" ");
                }
                tournament.setName(nameBuilder.substring(0, nameBuilder.length() - 1));
                sender.sendMessage(MCUtils.color("&eYou set the name of the tournament to &b" + tournament.getName()));
            } else if (args[0].equalsIgnoreCase("setpointsperwin") || args[0].equalsIgnoreCase("sppw")) {
                int value = getIntFromInput(args[1], sender);
                if (value == -1) {
                    return true;
                }
                tournament.setPointsPerWin(value);
                sender.sendMessage(MCUtils.color("&eYou set the points per win to &b" + value));
            } else if (args[0].equalsIgnoreCase("setpointsperkill") || args[0].equalsIgnoreCase("sppk")) {
                int value = getIntFromInput(args[1], sender);
                if (value == -1) {
                    return true;
                }
                tournament.setPointsPerKill(value);
                sender.sendMessage(MCUtils.color("&eYou set the points per kill to &b" + value));
            } else if (args[0].equalsIgnoreCase("setpointspersurvival") || args[0].equalsIgnoreCase("spps")) {
                int value = getIntFromInput(args[1], sender);
                if (value == -1) {
                    return true;
                }
                tournament.setPointsPerSurvival(value);
                sender.sendMessage(MCUtils.color("&eYou set the points per survival to &b" + value));
            } else if (args[0].equalsIgnoreCase("setservers")) {
                String[] servers = args[1].split(",");
                tournament.setServers(servers);
                sender.sendMessage(MCUtils.color("&eYou set the servers to &b" + StringHelper.join(servers, ",")));
            } else {
                UUID uuid = getUUIDFromInput(args[1], sender);
                if (uuid == null) {
                    return true;
                }
                
                NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(uuid);
                String name;
                if (nexusPlayer != null) {
                    name = nexusPlayer.getName();
                } else {
                    name = uuid.toString();
                }
                
                if (args[0].equalsIgnoreCase("sethost") || args[0].equalsIgnoreCase("sh")) {
                    tournament.setHost(uuid);
                    sender.sendMessage(MCUtils.color("&eYou set the host of the tournament to &e" + name));
                }
            }
            NexusAPI.getApi().getDataManager().pushTournament(tournament);
            NexusAPI.getApi().getNetworkManager().send("tournament", tournament.getId() + "");
        }
        
        return true;
    }
    
    private int getIntFromInput(String input, CommandSender sender) {
        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            sender.sendMessage(MCUtils.color("&cYou provided an invalid number value."));
            return -1;
        }
        
        if (value < 0) {
            sender.sendMessage(MCUtils.color("&cYou must provide a positive number or 0"));
            return -1;
        }
        
        return value;
    }
    
    private UUID getUUIDFromInput(String input, CommandSender sender) {
        UUID uuid;
        try {
            uuid = UUID.fromString(input);
        } catch (Exception e) {
            NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(input);
            if (nexusPlayer == null) {
                sender.sendMessage(MCUtils.color("&cA player with that name has not joined the server. Please provide a valid UUID"));
                return null;
            }
            
            uuid = nexusPlayer.getUniqueId();
        }
        return uuid;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
