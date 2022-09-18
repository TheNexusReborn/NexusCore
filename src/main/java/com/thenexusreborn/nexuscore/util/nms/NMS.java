package com.thenexusreborn.nexuscore.util.nms;

import org.bukkit.command.Command;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Abstract class for interacting with this API
 * <p> 
 * Must be instantiated with the NMS.getNMS() static method while providing the Version 
 * <p> 
 * The methods should be pretty self explanitory
 */
public abstract class NMS {
    
    public abstract double[] getRecentTPS();
    public abstract ItemStack removeNBTValue(ItemStack itemStack, String key);
    public abstract ItemStack addNBTString(ItemStack itemStack, String key, String value);
    public abstract String getNBTString(ItemStack itemStack, String key);
    public abstract ItemStack addNBTByte(ItemStack itemStack, String key, byte value);
    public abstract byte getNBTByte(ItemStack itemStack, String key);
    public abstract ItemStack addNBTShort(ItemStack itemStack, String key, short value);
    public abstract short getNBTShort(ItemStack itemStack, String key);
    public abstract ItemStack addNBTFloat(ItemStack itemStack, String key, float value);
    public abstract float getNBTFloat(ItemStack itemStack, String key);
    public abstract ItemStack addNBTInt(ItemStack itemStack, String key, int value);
    public abstract int getNBTInt(ItemStack itemStack, String key);
    public abstract ItemStack addNBTLong(ItemStack itemStack, String key, long value);
    public abstract long getNBTLong(ItemStack itemStack, String key);
    public abstract ItemStack addNBTDouble(ItemStack itemStack, String key, double value);
    public abstract double getNBTDouble(ItemStack itemStack, String key);
    public abstract ItemStack addNBTBoolean(ItemStack itemStack, String key, boolean value);
    public abstract boolean getNBTBoolean(ItemStack itemStack, String key);
    public abstract ItemStack addNBTUuid(ItemStack itemStack, String key, UUID value);
    public abstract UUID getNBTUuid(ItemStack itemStack, String key);
    public abstract void registerCommand(JavaPlugin plugin, Command command);

    public enum Version {
        MC_1_8_R3
    }
    
    public static NMS getNMS(Version version) {
        if (version == Version.MC_1_8_R3) {
            return new NMS_1_8_R3();
        }
        throw new UnsupportedOperationException("That version is unsupported");
    }
}