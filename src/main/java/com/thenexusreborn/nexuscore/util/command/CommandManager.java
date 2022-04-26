package com.thenexusreborn.nexuscore.util.command;

import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.collection.IncrementalMap;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommandManager implements TabExecutor {
    private NexusCore plugin;
    private List<NexusCommand> commands = new ArrayList<>();
    
    public CommandManager(NexusCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        NexusCommand starCommand = null;
        for (NexusCommand command : this.commands) {
            if (command.matchesName(label)) {
                starCommand = command;
            }
        }
        
        if (starCommand == null) {
            Bukkit.getServer().getLogger().warning("A Plugin Command with the name " + cmd.getName() + " is registered with the StarMCUtils CommandAPI for plugin " + plugin.getName() + " but does not have an implementation registered. Please contact the plugin author.");
            return true;
        }
        
        if (starCommand.isOnlyConsole() && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(MCUtils.color("&cOnly the console can use that command."));
            return true;
        }
        
        if (starCommand.isOnlyPlayer() && !(sender instanceof Player)) {
            sender.sendMessage(MCUtils.color("&cOnly players can use that command."));
            return true;
        }
        
        if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(MCUtils.color("&cOnly players and console can use that command."));
            return true;
        }
        
        if (starCommand.getMinRank() != null) {
            Rank actorRank;
            if (sender instanceof ConsoleCommandSender) {
                actorRank = Rank.ADMIN;
            } else {
                actorRank = NexusAPI.getApi().getPlayerManager().getNexusPlayer(((Player) sender).getUniqueId()).getRank();
            }
            
            if (actorRank.ordinal() > starCommand.getMinRank().ordinal()) {
                sender.sendMessage(MCUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
        }
        
        CommandActor actor = new CommandActor(sender);
        IncrementalMap<Argument> arguments = starCommand.getArguments();
        if (!arguments.isEmpty()) {
            for (int i = 0; i < arguments.size(); i++) {
                Argument argument = arguments.get(i);
                if (argument != null) {
                    if (argument.isRequired()) {
                        try {
                            String arg = args[i];
                            if (arg == null || arg.equals("")) {
                                throw new IllegalArgumentException();
                            }
                        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                            sender.sendMessage(MCUtils.color("&c" + argument.getErrorMessage()));
                            return true;
                        }
                    }
                }
            }
        }
        starCommand.handleCommand(actor, label, args);
        starCommand.handleSubCommands(actor, args);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        NexusCommand starCommand = null;
        for (NexusCommand command : this.commands) {
            if (command.matchesName(label)) {
                starCommand = command;
                break;
            }
        }
        
        if (starCommand == null) {
            return null;
        }
        
        if (starCommand.isOnlyPlayer() && !(sender instanceof Player)) {
            return null;
        }
        
        if (starCommand.isOnlyConsole() && !(sender instanceof ConsoleCommandSender)) {
            return null;
        }
        
        return getCompletions(starCommand, args, args.length - 1, 0);
    }
    
    private List<String> getCompletions(NexusCommand starCommand, String[] args, int argIndex, int cmdIndex) {
        if (argIndex < 0) {
            return null;
        }
        
        List<String> completions = new ArrayList<>();
        
        if (starCommand.getSubCommands().size() > 0 && starCommand.getArguments().size() == 0) {
            if (cmdIndex < argIndex) {
                cmdIndex++;
                for (SubCommand subCommand : starCommand.getSubCommands()) {
                    if (subCommand.matchesName(args[cmdIndex - 1])) {
                        completions.addAll(getCompletions(subCommand, args, argIndex, cmdIndex));
                    }
                }
            } else {
                for (SubCommand subCommand : starCommand.getSubCommands()) {
                    completions.add(subCommand.getName());
                }
            }
        } else if (starCommand.getSubCommands().size() == 0 && starCommand.getArguments().size() > 0) {
            int scArgIndex = argIndex - cmdIndex;
            for (Argument argument : starCommand.getArguments().values()) {
                if (argument.getIndex() == scArgIndex) {
                    if (argument.getCompletions().size() > 0) {
                        for (String completion : argument.getCompletions()) {
                            if (completion.startsWith(args[argIndex])) {
                                completions.add(completion);
                            }
                        }
                    } else {
                        char leadSymbol, endSymbol;
                        if (argument.isRequired()) {
                            leadSymbol = '<';
                            endSymbol = '>';
                        } else {
                            leadSymbol = '[';
                            endSymbol = ']';
                        }
                        
                        completions.add(leadSymbol + argument.getName() + endSymbol);
                    }
                }
            }
        }
        
        return completions;
    }
    
    public JavaPlugin getPlugin() {
        return plugin;
    }
    
    public List<NexusCommand> getCommands() {
        return commands;
    }
}