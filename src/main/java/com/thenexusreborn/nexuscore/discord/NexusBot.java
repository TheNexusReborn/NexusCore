package com.thenexusreborn.nexuscore.discord;

import com.thenexusreborn.nexuscore.NexusCore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class NexusBot {
    
    private JDABuilder builder;
    private JDA jda;
    
    private NexusCore plugin;
    
    private long publicDiscord, staffDiscord;
    
    public NexusBot(NexusCore plugin) {
        builder = JDABuilder.createDefault(plugin.getConfig().getString("discord.token"))
                .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES)
                .enableCache(CacheFlag.ACTIVITY, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.VOICE_STATE)
                .addEventListeners(new DiscordListener(plugin))
                .setMemberCachePolicy(MemberCachePolicy.ALL);
        this.plugin = plugin;
    }
    
    public void start() {
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
}