package com.thenexusreborn.nexuscore.api.command;

import com.stardevllc.cmdflags.CmdFlags;
import com.stardevllc.cmdflags.FlagResult;
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

        if (!(args.length > 0)) {
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
                return;
            }
            
            String sLabel = args[0];

            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length);

            args = newArgs;

            subCommand.onCommand(sender, senderRank, sLabel, args, flagResults);
        }
    }
    
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        return false;
    }

    public List<String> getCompletions(CommandSender sender, Rank senderRank, String label, String[] args) {
        return List.of();
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
}