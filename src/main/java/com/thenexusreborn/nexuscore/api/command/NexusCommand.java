package com.thenexusreborn.nexuscore.api.command;

import com.stardevllc.starcore.cmdflags.CmdFlags;
import com.stardevllc.starcore.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class NexusCommand<T extends JavaPlugin> implements ICommand<T>, TabExecutor {
    
    protected T plugin;
    
    protected String name;
    protected String[] aliases;
    protected String description;
    protected boolean playerOnly = false;
    protected boolean consoleOnly = false;
    
    protected Rank minRank;
    
    protected List<SubCommand<T>> subCommands = new ArrayList<>();
    
    protected CmdFlags cmdFlags = new CmdFlags();

    public NexusCommand(T plugin, String name, String description, Rank minRank, String... aliases) {
        this.plugin = plugin;

        PluginCommand pluginCommand = this.plugin.getCommand(name);
        
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
        
        this.name = name;
        this.description = description;
        this.minRank = minRank;
        this.aliases = aliases;
    }
    
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        return false;
    }
    
    public List<String> getCompletions(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (this.subCommands.isEmpty()) {
                completions.addAll(getCompletions(sender, senderRank, label, args, flagResults));
            } else {
                this.subCommands.forEach(scmd -> completions.add(scmd.getName().toLowerCase()));
            }
            String arg = args[0].toLowerCase();
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(arg));
        } else if (args.length > 1) {
            SubCommand<T> subCommand = getSubCommand(args[0]);
            if (subCommand != null) {
                String cmdLabel = args[0];
                
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                
                args = newArgs;
                
                completions.addAll(subCommand.getCompletions(sender, senderRank, cmdLabel, args));
            }
        }
        
        return completions;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (this.playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(MsgType.ERROR.format("Only players can use that command."));
            return true;
        }
        
        if (this.consoleOnly && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(MsgType.ERROR.format("Only console can use that command."));
            return true;
        }
        
        Rank senderRank = MCUtils.getSenderRank(sender);
        if (senderRank.ordinal() > minRank.ordinal()) {
            sender.sendMessage(MsgType.WARN.format("You must have the rank " + this.minRank.getColor() + "&l" + this.minRank.name() + " %bto use that command."));
            return true;
        }

        FlagResult flagResults = cmdFlags.parse(args);
        args = flagResults.args();
        
        //If the execute command returns false, the handle the configured sub commands
        if (execute(sender, senderRank, label, args, flagResults)) {
            return true;
        }
        
        if (args.length > 0) {
            SubCommand<T> subCommand = getSubCommand(args[0]);
            
            if (subCommand == null) {
                sender.sendMessage(MsgType.WARN.format("Unable to find a subcommand matching %v.", args[0]));
                return true;
            }
            
            String cmdLabel = args[0];

            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            args = newArgs;
            
            subCommand.onCommand(sender, senderRank, cmdLabel, args, flagResults);
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = MCUtils.getSenderRank(sender);
        if (senderRank.ordinal() > minRank.ordinal()) {
            return List.of();
        }
        
        FlagResult flagResult = this.cmdFlags.parse(args);
        args = flagResult.args();
        
        return getCompletions(sender, senderRank, label, args, flagResult);
    }
    
    public SubCommand<T> getSubCommand(String name) {
        for (SubCommand<T> subCommand : this.subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
            
            if (subCommand.getAliases() == null) {
                continue;
            }

            for (String alias : subCommand.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return subCommand;
                }
            }
        }
        
        return null;
    }

    public CmdFlags getCmdFlags() {
        return cmdFlags;
    }

    @Override
    public T getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Rank getMinRank() {
        return minRank;
    }

    public List<SubCommand<T>> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean isPlayerOnly() {
        return playerOnly;
    }
}