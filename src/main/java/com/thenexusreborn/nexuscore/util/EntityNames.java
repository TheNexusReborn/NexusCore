package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.api.helper.StringHelper;
import org.bukkit.entity.EntityType;

import java.util.*;

/**
 * A utility to map the entitytype enum to formatted names
 */
public class EntityNames {
    public Map<EntityType, String> entityNames = new HashMap<>();
    private static final EntityNames instance = new EntityNames() {
        @Override
        public void setName(EntityType entityType, String name) {
            throw new RuntimeException("Cannot set the entity name using the default instance");
        }
    };
    
    /**
     * Creates a custom instance of this class that can be used to set custom names
     * @return A custom instance of this class
     */
    public static EntityNames createInstance() {
        return new EntityNames();
    }
    
    /**
     * Gets the global instance of this class. This instance cannot have any names changed.
     * @return The global instance
     */
    public static EntityNames getInstance() {
        return instance;
    }

    private EntityNames() {
        for (EntityType entityType : EntityType.values()) {
            entityNames.put(entityType, StringHelper.capitalizeEveryWord(entityType.name()));
        }
    }
    
    /**
     * Gets the default instance of the entity name
     * @param entityType The entity type
     * @return The name using the default instance. This is just a wrapper method
     */
    public static String getDefaultName(EntityType entityType) {
        return getInstance().getName(entityType);
    }
    
    public String getName(EntityType entityType) {
        return entityNames.get(entityType);
    }
    
    public void setName(EntityType entityType, String name) {
        entityNames.put(entityType, name);
    }
}
