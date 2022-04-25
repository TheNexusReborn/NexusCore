package com.thenexusreborn.nexuscore.util.nms;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

class NMS_1_8_R3 extends NMS {
    @Override
    public double[] getRecentTPS() {
        return ((CraftServer) Bukkit.getServer()).getServer().recentTps;
    }
    
    @Override
    public ItemStack removeNBTValue(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.remove(key);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public ItemStack addNBTString(ItemStack itemStack, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setString(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public String getNBTString(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getString(key);
    }
    
    @Override
    public ItemStack addNBTByte(ItemStack itemStack, String key, byte value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setByte(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public byte getNBTByte(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getByte(key);
    }
    
    @Override
    public ItemStack addNBTShort(ItemStack itemStack, String key, short value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setShort(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public short getNBTShort(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getShort(key);
    }
    
    @Override
    public ItemStack addNBTFloat(ItemStack itemStack, String key, float value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setFloat(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public float getNBTFloat(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getFloat(key);
    }
    
    @Override
    public ItemStack addNBTInt(ItemStack itemStack, String key, int value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setInt(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public int getNBTInt(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getInt(key);
    }
    
    @Override
    public ItemStack addNBTLong(ItemStack itemStack, String key, long value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setLong(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public long getNBTLong(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getLong(key);
    }
    
    @Override
    public ItemStack addNBTDouble(ItemStack itemStack, String key, double value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setDouble(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public double getNBTDouble(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getDouble(key);
    }
    
    @Override
    public ItemStack addNBTBoolean(ItemStack itemStack, String key, boolean value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setBoolean(key, value);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public boolean getNBTBoolean(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return tag.getBoolean(key);
    }
    
    @Override
    public ItemStack addNBTUuid(ItemStack itemStack, String key, UUID value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        tag.setString(key, value.toString());
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    
    @Override
    public UUID getNBTUuid(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = getOrCreateTag(nmsStack);
        return UUID.fromString(tag.getString(key));
    }
    
    @Override
    public void registerCommand(JavaPlugin plugin, Command command) {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        SimpleCommandMap commandMap = craftServer.getCommandMap();
        commandMap.register(plugin.getName(), command);
    }
    
    private NBTTagCompound getOrCreateTag(net.minecraft.server.v1_8_R3.ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTag(tag);
        }
        return tag;
    }
}