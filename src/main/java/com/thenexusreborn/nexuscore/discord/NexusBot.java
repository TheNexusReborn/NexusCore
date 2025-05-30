package com.thenexusreborn.nexuscore.discord;

import com.thenexusreborn.nexuscore.NexusCore;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class NexusBot {
    
    private JDABuilder builder;
    private JDA jda;
    
    private NexusCore plugin;
    
    private long publicDiscord, staffDiscord;
    
    private long serverStatusCategory;
    
    private Map<String, Long> serverChannels = new HashMap<>();
    
    public NexusBot(NexusCore plugin) {
        if (!plugin.getConfig().contains("discord.token") || plugin.getConfig().getString("discord.token").isBlank()) {
            return;
        }
        
        builder = JDABuilder.createDefault(plugin.getConfig().getString("discord.token"))
                .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES)
                .enableCache(CacheFlag.ACTIVITY, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.VOICE_STATE)
                .addEventListeners(new DiscordListener(plugin))
                .setMemberCachePolicy(MemberCachePolicy.ALL);
        this.plugin = plugin;
    }
    
    public void start() {
        if (this.plugin == null) {
            return;
        }
        
        if (jda != null) {
            return;
        }
        
        this.publicDiscord = plugin.getConfig().getLong("discord.public");
        this.staffDiscord = plugin.getConfig().getLong("discord.staff");
        
        if (this.publicDiscord == 0) {
            plugin.getLogger().warning("[NexusBot] The public discord id is not yet set.");
        }
        
        if (this.staffDiscord == 0) {
            plugin.getLogger().warning("[NexusBot] THe staff discord id is not yet set.");
        }

        if (this.builder == null) {
            return;
        }
        
        jda = builder.build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            jda = null;
            return;
        }
        
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
    }
    
    public boolean isStarted() {
        return jda != null;
    }
    
    public void shutdown() {
        if (jda != null) {
            jda.getPresence().setStatus(OnlineStatus.OFFLINE);
            jda.shutdown();
            jda = null;
            
            plugin.getConfig().set("discord.public", this.publicDiscord);
            plugin.getConfig().set("discord.staff", this.staffDiscord);
            plugin.saveConfig();
        }
    }

    public long getPublicDiscord() {
        return publicDiscord;
    }

    public void setPublicDiscord(long publicDiscord) {
        this.publicDiscord = publicDiscord;
    }

    public long getStaffDiscord() {
        return staffDiscord;
    }

    public void setStaffDiscord(long staffDiscord) {
        this.staffDiscord = staffDiscord;
    }
    
    public void setServerStatusCategory(long serverStatusCategory) {
        this.serverStatusCategory = serverStatusCategory;
    }
    
    public long getServerStatusCategory() {
        return serverStatusCategory;
    }
    
    public long addServerChannel(String name) {
        Guild publicDiscord = jda.getGuildById(this.publicDiscord);
        Category statusCategroy = publicDiscord.getCategoryById(this.serverStatusCategory);
        for (TextChannel textChannel : statusCategroy.getTextChannels()) {
            if (textChannel.getName().equalsIgnoreCase(name)) {
                this.serverChannels.put(name.toLowerCase(), textChannel.getIdLong());
                return textChannel.getIdLong();
            }
        }
        
        Role memberRole = null;
        for (Role role : publicDiscord.getRoles()) {
            if (role.getName().equalsIgnoreCase("member")) {
                memberRole = role;
            }
        }
        
        AtomicLong id = new AtomicLong();
        statusCategroy.createTextChannel(name).addPermissionOverride(memberRole, List.of(Permission.VIEW_CHANNEL), List.of(Permission.MESSAGE_SEND)).queue(channel -> id.set(channel.getIdLong()));
        
        this.serverChannels.put(name.toLowerCase(), id.get());
        return id.get();
    }
    
    public TextChannel getServerChannel(String name) {
        long channelId;
        if (!this.serverChannels.containsKey(name.toLowerCase())) {
            channelId = addServerChannel(name);
        } else {
            channelId = this.serverChannels.get(name.toLowerCase());
        }
        
        Guild guild = jda.getGuildById(this.publicDiscord);
        return guild.getTextChannelById(channelId);
    }
}