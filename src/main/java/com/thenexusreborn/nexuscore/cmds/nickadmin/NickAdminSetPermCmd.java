package com.thenexusreborn.nexuscore.cmds.nickadmin;

import com.stardevllc.starlib.helper.ReflectionHelper;
import com.stardevllc.starmclib.cmdflags.FlagResult;
import com.thenexusreborn.api.NexusReborn;
import com.thenexusreborn.api.nickname.NickPerms;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import com.thenexusreborn.nexuscore.util.MsgType;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NickAdminSetPermCmd extends SubCommand<NexusCore> {
    
    private static final Map<String, Field> fields = new HashMap<>();
    
    static {
        for (Field field : ReflectionHelper.getClassFields(NickPerms.class)) {
            field.setAccessible(true);
            fields.put(field.getName().toLowerCase(), field);
        }
    }
    
    public NickAdminSetPermCmd(NexusCore plugin, ICommand<NexusCore> parent) {
        super(plugin, parent, 0, "setpermission", "Sets a permission for nickname features", Rank.ADMIN);
    }
    
    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        if (!(args.length > 1)) {
            MsgType.WARN.send(sender, "You must provide a setting name and a new value.");
            return true;
        }
        
        String settingName = args[0].toLowerCase();
        
        Field field = null;
        for (Entry<String, Field> entry : fields.entrySet()) {
            if (entry.getKey().equals(settingName)) {
                field = entry.getValue();
                break;
            }
        }
        
        if (field == null) {
            MsgType.WARN.send(sender, "Could not find a setting with the name %v.", args[0]);
            return true;
        }
        
        Rank rank;
        try {
            rank = Rank.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            MsgType.WARN.send(sender, "Could not find a rank with the name %v.", args[1]);
            return true;
        }
        
        try {
            field.set(NexusReborn.getNickPerms(), rank);
            MsgType.INFO.send(sender, "You set the nick permission %v to have a minimum rank of %v", args[0], rank.getColor() + rank.name().replace("_", " "));
        } catch (IllegalAccessException e) {
            MsgType.ERROR.send(sender, "There was an error while setting the permission. Please report to Firestar311.");
            return true;
        }
        
        return true;
    }
}
