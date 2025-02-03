package com.thenexusreborn.nexuscore.api.command;

import com.stardevllc.cmdflags.CmdFlags;
import com.stardevllc.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class NexusCommand<T extends JavaPlugin> implements ICommand<T>, TabExecutor {
    
    protected T plugin;
    
    protected String name;
    protected String[] aliases;
    protected String description;
    
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
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
            SubCommand<T> subCommand = null;
            for (SubCommand<T> sc : this.subCommands) {
                if (args[0].equalsIgnoreCase(sc.getName())) {
                    subCommand = sc;
                    break;
                }
                
                if (aliases == null) {
                    continue;
                }

                for (String alias : sc.getAliases()) {
                    if (args[0].equalsIgnoreCase(alias)) {
                        subCommand = sc;
                        break;
                    }
                }
            }
            
            if (subCommand == null) {
                sender.sendMessage(MsgType.WARN.format("Unable to find subcommand matching %v.", args[0]));
                return true;
            }

            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            args = newArgs;
            
            subCommand.onCommand(sender, senderRank, args[0], args, flagResults);
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return List.of();
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
}