package com.thenexusreborn.nexuscore.api.command;

import com.stardevllc.starmclib.cmdflags.CmdFlags;
import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class SubCommand<T extends JavaPlugin> implements ICommand<T> {
    
    protected T plugin;
    
    protected ICommand<T> parent;
    
    protected int index;
    protected String name;
    protected String[] aliases;
    protected String description;
    
    protected Rank minRank;
    
    protected List<SubCommand<T>> subCommands = new ArrayList<>();
    
    protected CmdFlags cmdFlags = new CmdFlags();
    
    protected boolean playerOnly;

    public SubCommand(T plugin, ICommand<T> parent, int index, String name, String description, Rank minRank, String... aliases) {
        this.plugin = plugin;
        this.parent = parent;
        this.index = index;
        this.name = name;
        this.description = description;
        this.minRank = minRank;
        this.aliases = aliases;
    }
    
    public void onCommand(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult parentFlagResults) {
        if (senderRank.ordinal() > minRank.ordinal()) {
            sender.sendMessage(MsgType.WARN.format("You must have the rank " + this.minRank.getColor() + "&l" + this.minRank.name() + " %bto use that command."));
            return;
        }

        FlagResult flagResults = cmdFlags.parse(args);
        args = flagResults.args();
        
        flagResults.addFrom(parentFlagResults);

        //If the execute command returns false, the handle the configured sub commands
        if (execute(sender, senderRank, label, args, flagResults)) {
            return;
        }

        if (args.length > 0) {
            SubCommand<T> subCommand = getSubCommand(args[0]);

            if (subCommand == null) {
                sender.sendMessage(MsgType.WARN.format("Unable to find subcommand matching %v.", args[0]));
                return;
            }
            
            String sLabel = args[0];

            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            args = newArgs;
            
            subCommand.onCommand(sender, senderRank, sLabel, args, flagResults);
        }
    }
    
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        return false;
    }

    public List<String> getCompletions(CommandSender sender, Rank senderRank, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (this.subCommands.isEmpty()) {
                completions.addAll(getCompletions(sender, senderRank, label, args));
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
    
    public SubCommand<T> getSubCommand(String label) {
        for (SubCommand<T> subCommand : this.subCommands) {
            if (subCommand.getName().equalsIgnoreCase(label)) {
                return subCommand;
            }
            
            if (subCommand.getAliases() == null) {
                continue;
            }
            
            for (String alias : subCommand.getAliases()) {
                if (alias.equalsIgnoreCase(label)) {
                    return subCommand;
                }
            }
        }
        
        return null;
    }
    
    public T getPlugin() {
        return plugin;
    }

    public ICommand<T> getParent() {
        return parent;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public Rank getMinRank() {
        return minRank;
    }

    public List<SubCommand<T>> getSubCommands() {
        return subCommands;
    }

    public boolean isPlayerOnly() {
        if (this.parent.isPlayerOnly()) {
            return true;
        }
        
        return this.playerOnly;
    }
}