package com.thenexusreborn.nexuscore.discord;

import com.stardevllc.starlib.misc.CodeGenerator;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            guild.updateCommands().addCommands(Commands.slash("mclink", "Link your Discord Account to your Minecraft Account.")).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.getHook().setEphemeral(true);
        if (event.getName().equals("mclink")) {
            event.deferReply().setEphemeral(true).queue();

            try {
                List<NexusPlayer> results = NexusAPI.getApi().getPrimaryDatabase().get(NexusPlayer.class, "discordid", event.getMember().getId());
                if (!results.isEmpty()) {
                    event.getHook().sendMessage("You have already linked your Discord and Minecraft accounts together.");
                    return;
                }

                DiscordVerifyCode discordVerifyCode = new DiscordVerifyCode(event.getMember().getId(), CodeGenerator.generate(16));
                event.getHook().sendMessage("Please type /verify " + discordVerifyCode.getCode() + " on the server to finish linking.").setEphemeral(true).queue();
            } catch (Exception e) {
                event.getHook().sendMessage("There was an error processing your request.");
                e.printStackTrace();
            }
        }
    }
}