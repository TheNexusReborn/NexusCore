package com.thenexusreborn.nexuscore.nickname;

import com.stardevllc.starmclib.skin.Skin;
import dev.iiahmed.disguise.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class DisguiseWrapper {
    private DisguiseProvider provider;
    
    private DisguiseProvider getProvider() {
        if (this.provider == null) {
            this.provider = Bukkit.getServer().getServicesManager().getRegistration(DisguiseProvider.class).getProvider();
        }
        
        return this.provider;
    }
    
    public DisguiseResponse unnick(Player player) {
        return setDisguise(player, null, null, null);
    }
    
    public DisguiseResponse setNick(Player player, String name, Skin skin) {
        return setDisguise(player, name, skin, null);
    }
    
    public DisguiseResponse setEntity(Player player, EntityType entityType) {
        return setDisguise(player, null, null, entityType);
    }
    
    public DisguiseResponse resetEntity(Player player) {
        return setDisguise(player, null, null, EntityType.PLAYER);
    }
    
    public DisguiseResponse setDisguise(Player player, String name, Skin skin, EntityType entityType) {
        System.out.println("Setting disguise for " + player.getName());
        DisguiseProvider provider = getProvider();
        //Get current info
        PlayerInfo currentInfo = provider.getInfo(player);
        //Create a new builder
        Disguise.Builder disguiseBuilder = Disguise.builder();
        disguiseBuilder.setName(currentInfo.getNickname());
        
        //If a name is provided, then override current name
        if (name != null && !name.isBlank()) {
            disguiseBuilder.setName(name);
        }
        
        if (currentInfo.hasSkin()) {
            disguiseBuilder.setSkin(currentInfo.getSkin());
        }
        
        //If a skin is provided, then override the existing skin
        if (skin != null) {
            disguiseBuilder.setSkin(skin.getValue(), skin.getSignature());
        }
        
        //Checking for an existing entity
        if (currentInfo.hasEntity()) {
            disguiseBuilder.setEntity(builder -> builder.setType(currentInfo.getEntityType()));
        }
        
        //If entity type is provided in the parameters, override current info
        if (entityType != null) {
            disguiseBuilder.setEntity(builder -> builder.setType(entityType));
        }
        
        //Create new disguise (It is pretty light as it doesn't do much on it's own)
        Disguise disguise = disguiseBuilder.build();
        
        System.out.println("Disguise Name: " + disguise.getName());
        System.out.println("Disguise Entity: " + (disguise.getEntity() != null ? disguise.getEntity().getType() : null));
        
        //Undisguise to prevent realname issues
        if (provider.isDisguised(player)) {
            System.out.println("Undisguised");
            provider.undisguise(player);
        }
        
        //Check to see if all three fields are empty, if they are, don't issue another disguise as we are just resetting to default name at this point
        if (!disguise.hasName() && !disguise.hasSkin() && (!disguise.hasEntity() || entityType == EntityType.PLAYER)) {
            System.out.println("All fields empty, returning success");
            return DisguiseResponse.SUCCESS;
        }
        
        //Disguise the player with the disguise information and return response value
        DisguiseResponse response = provider.disguise(player, disguise);
        System.out.println("Response: " + response);
        return response;
    }
}
