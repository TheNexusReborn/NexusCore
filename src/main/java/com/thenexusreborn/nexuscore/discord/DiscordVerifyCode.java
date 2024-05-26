package com.thenexusreborn.nexuscore.discord;

public class DiscordVerifyCode {
    private String discordId;
    private String code;

    public DiscordVerifyCode(String discordId, String code) {
        this.discordId = discordId;
        this.code = code;
    }

    public String getDiscordId() {
        return discordId;
    }

    public String getCode() {
        return code;
    }
}