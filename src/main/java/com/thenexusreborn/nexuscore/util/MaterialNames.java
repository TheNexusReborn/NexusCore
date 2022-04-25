package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.nexuscore.util.helper.StringHelper;
import org.bukkit.Material;

import java.util.*;

/**
 * A utility to map materials to a formatted name
 */
public class MaterialNames {
    private final Map<Material, String> materialNames = new HashMap<>();
    private static final MaterialNames instance = new MaterialNames() {
        @Override
        public void setName(Material material, String name) {
            throw new RuntimeException("Could not set the material name using the default instance.");
        }
    };
    
    /**
     * Gets the default instance 
     * @return Default instance
     */
    public static MaterialNames getInstance() {
        return instance;
    }
    
    /**
     * Creates a custom instance
     * @return Newly created custom instance
     */
    public static MaterialNames createInstance() {
        return new MaterialNames();
    }

    private MaterialNames() {
        for (Material material : Material.values()) {
            materialNames.put(material, StringHelper.capitalizeEveryWord(material.name()));
        }
    }
    
    /**
     * Gets the name
     * @param material The material
     * @return The formatted name
     */
    public String getName(Material material) {
        if (material == null) {
            return "None";
        }
        return (materialNames.getOrDefault(material, "None"));
    }
    
    /**
     * Gets the name in the default instance
     * @param material The material
     * @return The name
     */
    public static String getDefaultName(Material material) {
        return getInstance().getName(material);
    }
    
    /**
     * Sets the name of a material
     * Cannot override the name of a material on the default instance
     * @param material The material
     * @param name The new name
     */
    public void setName(Material material, String name) {
        this.materialNames.put(material, name);
    }
}
