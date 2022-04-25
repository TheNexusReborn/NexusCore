package com.thenexusreborn.nexuscore.util.command;

import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandActor {
    private CommandSender sender;
    
    public CommandActor(CommandSender sender) {
        this.sender = sender;
    }
    
    public CommandSender getSender() {
        return sender;
    }
    
    public void sendUncoloredMessage(String message) {
        sender.sendMessage(message);
    }
    
    public void sendMessage(String message) {
        sender.sendMessage(MCUtils.color(message));
    }
    
    public Player getPlayer() {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        
        return null;
    }
    
    public ConsoleCommandSender getConsoleSender() {
        if (sender instanceof ConsoleCommandSender) {
            return (ConsoleCommandSender) sender;
        }
        
        return null;
    }
}
