package com.thenexusreborn.nexuscore.cmds.tag;

import com.stardevllc.cmdflags.FlagResult;
import com.stardevllc.colors.StarColors;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.player.Rank;
import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.api.command.ICommand;
import com.thenexusreborn.nexuscore.api.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class TagSubCommand extends SubCommand<NexusCore> {
    public TagSubCommand(NexusCore plugin, ICommand<NexusCore> parent, String name, String description, String... aliases) {
        super(plugin, parent, 0, name, description, Rank.MEMBER, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, Rank senderRank, String label, String[] args, FlagResult flagResults) {
        Player player = (Player) sender;

        NexusPlayer nexusPlayer = NexusAPI.getApi().getPlayerManager().getNexusPlayer(player.getUniqueId());
        if (nexusPlayer == null) {
            player.sendMessage(StarColors.color("&cPlease wait for your data to load before using this command."));
            return true;
        }
        
        handle(nexusPlayer, args);
        return true;
    }
    
    protected abstract void handle(NexusPlayer nexusPlayer, String[] args);
}
