package com.thenexusreborn.nexuscore.discord;

import com.stardevllc.helper.CodeGenerator;
import com.thenexusreborn.api.NexusAPI;
import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.nexuscore.NexusCore;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;

import java.util.List;

public class DiscordListener extends ListenerAdapter {
    
    private NexusCore plugin;

    public DiscordListener(NexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            guild.updateCommands().addCommands(
                    Commands.slash("mclink", "Link your Discord Account to your Minecraft Account."), 
                    Commands.slash("settype", "Sets the type of this guild for Nexus Reborn.")
                            .addOptions(new OptionData(OptionType.STRING, "type", "The type", true)
                                    .addChoice("Public", "public")
                                    .addChoice("Staff", "staff"))
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                    )
                    .queue();
            
            if (guild.getIdLong() == plugin.getNexusBot().getPublicDiscord()) {
                boolean hasStatusCategory = false;
                for (Category category : guild.getCategories()) {
                    if (category.getName().equalsIgnoreCase("server status")) {
                        hasStatusCategory = true;
                    }
                    
                    //TODO Additional Checks
                    
                    
                }
                
                if (!hasStatusCategory) {
                    guild.createCategory("Server Status").queue(category -> {
                        ChannelOrderAction orderAction = guild.modifyCategoryPositions();
                        orderAction.selectPosition(category);
                        orderAction.moveTo(1);
                        orderAction.queue();
                    });
                }
            }
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
        } else if (event.getName().equalsIgnoreCase("settype")) {
            if (event.getOption("public").getAsString().equalsIgnoreCase("public")) {
                plugin.getNexusBot().setPublicDiscord(event.getGuild().getIdLong());
                event.reply("You set this server to be the public type").setEphemeral(true);
            } else if (event.getOption("staff").getAsString().equalsIgnoreCase("staff")) {
                plugin.getNexusBot().setStaffDiscord(event.getGuild().getIdLong());
                event.reply("You set this server to be the staff type").setEphemeral(true);
            }
        }
    }
}