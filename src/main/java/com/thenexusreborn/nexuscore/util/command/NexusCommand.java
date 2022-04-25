package com.thenexusreborn.nexuscore.util.command;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.player.Rank;
import com.thenexusreborn.nexuscore.util.collection.IncrementalMap;
import com.thenexusreborn.nexuscore.util.nms.NMS;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Represents a command for the command framework
 */
@SuppressWarnings("DuplicatedCode")
public class NexusCommand {
    
    protected static final NMS nms = NexusCore.getPlugin(NexusCore.class).getNMS();
    
    protected final String name, description;
    protected final Rank minRank;
    protected final List<String> aliases;
    protected final boolean onlyPlayer, onlyConsole;
    protected final List<SubCommand> subCommands = new ArrayList<>();
    protected final IncrementalMap<Argument> arguments = new IncrementalMap<>();
    
    public NexusCommand(String name, String description, Rank minRank) {
        this(name, description, minRank, new ArrayList<>());
    }
    
    public NexusCommand(String name, String description, Rank minRank, List<String> aliases) {
        this(name, description, minRank, false, false, aliases);
    }
    
    public NexusCommand(String name, String description, Rank minRank, boolean onlyPlayer, boolean onlyConsole, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.minRank = minRank;
        this.aliases = aliases;
        this.onlyPlayer = onlyPlayer;
        this.onlyConsole = onlyConsole;
    }
    
    public void handleCommand(CommandActor actor, String label, String[] args) {
        
    }
    
    public void addArgument(Argument argument) {
        int index = this.arguments.add(argument);
        argument.setIndex(index);
    }
    
    public void setArgument(int index, Argument argument) {
        this.arguments.put(index, argument);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Rank getMinRank() {
        return minRank;
    }
    
    public List<String> getAliases() {
        return aliases;
    }
    
    public boolean isOnlyPlayer() {
        return onlyPlayer;
    }
    
    public boolean isOnlyConsole() {
        return onlyConsole;
    }
    
    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
    
    public void register(CommandManager commandManager) {
        JavaPlugin plugin = commandManager.getPlugin();
        PluginCommand pluginCommand = plugin.getCommand(this.name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandManager);
            pluginCommand.setTabCompleter(commandManager);
        } else {
            BukkitCommand bukkitCommand = new BukkitCommand(plugin, this);
            bukkitCommand.setExecutor(commandManager);
            bukkitCommand.setCompleter(commandManager);
            nms.registerCommand(plugin, bukkitCommand);
        }
        commandManager.getCommands().add(this);
    }
    
    public boolean matchesName(String label) {
        if (label.equalsIgnoreCase(this.name)) {
            return true;
        } else {
            for (String alias : this.aliases) {
                if (label.equalsIgnoreCase(alias)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getUsage() {
        return "";
    }
    
    public void addSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
    }
    
    public void handleSubCommands(CommandActor actor, String[] args) {
        if (this.subCommands.isEmpty()) {
            return;
        }
        
        if (args.length == 0) {
            actor.sendMessage("&cYou must provide a sub command.");
            return;
        }
        
        String[] previousArgs = new String[0];
        String label = args[0];
        String[] afterArgs = new String[args.length - 1];
        if (args.length > 1) {
            System.arraycopy(args, 1, afterArgs, 0, args.length - 1);
        }
        
        for (SubCommand subCommand : this.subCommands) {
            if (subCommand.matchesName(label)) {
                IncrementalMap<Argument> arguments = subCommand.getArguments();
                if (!arguments.isEmpty()) {
                    for (int i = 0; i < arguments.size(); i++) {
                        Argument argument = arguments.get(i);
                        if (argument != null) {
                            if (argument.isRequired()) {
                                try {
                                    String arg = afterArgs[i];
                                    if (arg == null || arg.equals("")) {
                                        throw new IllegalArgumentException();
                                    }
                                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                                    actor.sendMessage("&c" + argument.getErrorMessage());
                                    return;
                                }
                            }
                        }
                    }
                }
                subCommand.handleCommand(this, actor, previousArgs, label, afterArgs);
                subCommand.handleSubCommands(actor, previousArgs, label, afterArgs);
            }
        }
    }
    
    public IncrementalMap<Argument> getArguments() {
        return arguments;
    }
}
