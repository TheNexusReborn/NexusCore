package com.thenexusreborn.nexuscore.chat;

import com.stardevllc.starchat.channels.ChatChannel;
import com.stardevllc.starchat.context.ChatContext;
import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.api.punishment.Punishment;
import com.thenexusreborn.api.punishment.PunishmentType;
import com.thenexusreborn.nexuscore.NexusCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

public class PunishmentChannel extends ChatChannel {
    public PunishmentChannel(NexusCore plugin) {
        super(plugin, "punishments", new File(plugin.getDataFolder(), "channels" + File.separator + "punishments.yml").toPath());
        
        this.viewPermission.set("nexuscore.punishments.notify");
        this.systemFormat.set("&6({origin}) &d{target} &fwas {type} &fby &b{actor} &ffor &3{reason}{length}");
    }

    @Override
    public void sendMessage(ChatContext context) {
        //no-op
    }

    private String formatLength(Punishment punishment, String message) {
        if (punishment.getType() != PunishmentType.WARN && punishment.getType() != PunishmentType.KICK) {
            return message.replace("{length}", " &c(" + punishment.formatTimeLeft() + ")");
        } else {
            return message.replace("{length}", "");
        }
    }
    
    public void sendPunishmentRemoval(Punishment punishment) {
        String message = this.systemFormat.get().replace("{origin}", punishment.getServer()).replace("{target}", punishment.getTargetNameCache())
                .replace("{type}", punishment.getType().getColor() + "un" + punishment.getType().getVerb())
                .replace("{actor}", punishment.getActorNameCache()).replace("{reason}", punishment.getReason());
       message = formatLength(punishment, message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(this.viewPermission.get())) {
                player.sendMessage(ColorUtils.color(message));
            }
        }
    }
    
    public void sendPunishment(Punishment punishment) {
        String message = this.systemFormat.get().replace("{origin}", punishment.getServer()).replace("{target}", punishment.getTargetNameCache())
                .replace("{type}", punishment.getType().getColor() + punishment.getType().getVerb())
                .replace("{actor}", punishment.getActorNameCache()).replace("{reason}", punishment.getReason());
        message = formatLength(punishment, message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(this.viewPermission.get())) {
                player.sendMessage(ColorUtils.color(message));
            }
        }
    }
}
