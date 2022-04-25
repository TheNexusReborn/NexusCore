package com.thenexusreborn.nexuscore.util.command;

import org.apache.commons.lang.Validate;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BukkitCommand extends Command {
    
    private final JavaPlugin plugin;
    private CommandExecutor executor;
    private TabCompleter completer;
    
    protected BukkitCommand(JavaPlugin plugin, NexusCommand starCommand) {
        super(starCommand.getName(), starCommand.getDescription(), starCommand.getUsage(), starCommand.getAliases());
        this.plugin = plugin;
    }
    
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }
    
    public void setCompleter(TabCompleter completer) {
        this.completer = completer;
    }
    
    public CommandExecutor getExecutor() {
        return executor;
    }
    
    public TabCompleter getCompleter() {
        return completer;
    }
    
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean success;
        if (!this.plugin.isEnabled()) {
            throw new CommandException("Cannot execute command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName() + " - plugin is disabled.");
        } else if (!this.testPermission(sender)) {
            return true;
        } else {
            try {
                success = this.executor.onCommand(sender, this, commandLabel, args);
            } catch (Throwable throwable) {
                throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName(), throwable);
            }
        
            if (!success && this.usageMessage.length() > 0) {
                String[] var8;
                int var7 = (var8 = this.usageMessage.replace("<command>", commandLabel).split("\n")).length;
            
                for(int var6 = 0; var6 < var7; ++var6) {
                    String line = var8[var6];
                    sender.sendMessage(line);
                }
            }
        
            return success;
        }
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        List completions = null;
        
        try {
            if (this.completer != null) {
                completions = this.completer.onTabComplete(sender, this, alias, args);
            }
            
            if (completions == null && this.executor instanceof TabCompleter) {
                completions = ((TabCompleter)this.executor).onTabComplete(sender, this, alias, args);
            }
        } catch (Throwable var11) {
            StringBuilder message = new StringBuilder();
            message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
    
            for (String arg : args) {
                message.append(arg).append(' ');
            }
            
            message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.plugin.getDescription().getFullName());
            throw new CommandException(message.toString(), var11);
        }
        
        return completions == null ? super.tabComplete(sender, alias, args) : completions;
    }
    
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(", ").append(this.plugin.getDescription().getFullName()).append(')');
        return stringBuilder.toString();
    }
}
