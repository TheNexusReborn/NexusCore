package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.nexuscore.util.helper.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.*;

/**
 * A class to represent a YAML config file and adds passthrough methods and easy methods to setup and save
 */
public class Config {
    private File file;
    private YamlConfiguration yamlConfiguration;
    
    private JavaPlugin plugin;
    private String name, folder = "";
    
    private Map<String, Object> defaultValues = new HashMap<>();
    
    public Config(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }
    
    public Config(JavaPlugin plugin, String folder, String name) {
        this.plugin = plugin;
        this.folder = folder;
        this.name = name;
    }
    
    public void addDefaultValue(String path, Object object) {
        this.defaultValues.put(path, object);
    }
    
    public File getFile() {
        return file;
    }
    
    public Map<String, Object> getDefaultValues() {
        return new HashMap<>(defaultValues);
    }
    
    public boolean contains(String path) {
        return yamlConfiguration.contains(path);
    }
    
    public ConfigurationSection createSection(String path) {
        return yamlConfiguration.createSection(path);
    }
    
    public ConfigurationSection createSection(String path, Map<?, ?> values) {
        return yamlConfiguration.createSection(path, values);
    }
    
    public Object get(String path) {
        return yamlConfiguration.get(path);
    }
    
    public boolean getBoolean(String path) {
        return yamlConfiguration.getBoolean(path);
    }
    
    public List<Boolean> getBooleanList(String path) {
        return yamlConfiguration.getBooleanList(path);
    }
    
    public List<Byte> getByteList(String path) {
        return yamlConfiguration.getByteList(path);
    }
    
    public List<Character> getCharacterList(String path) {
        return yamlConfiguration.getCharacterList(path);
    }
    
    public ConfigurationSection getConfigurationSection(String path) {
        return yamlConfiguration.getConfigurationSection(path);
    }
    
    public double getDouble(String path) {
        return yamlConfiguration.getDouble(path);
    }
    
    public List<Double> getDoubleList(String path) {
        return yamlConfiguration.getDoubleList(path);
    }
    
    public List<Float> getFloatList(String path) {
        return yamlConfiguration.getFloatList(path);
    }
    
    public int getInt(String path) {
        return yamlConfiguration.getInt(path);
    }
    
    public List<Integer> getIntegerList(String path) {
        return yamlConfiguration.getIntegerList(path);
    }
    
    public ItemStack getItemStack(String path) {
        return yamlConfiguration.getItemStack(path);
    }
    
    public Set<String> getKeys(boolean deep) {
        return yamlConfiguration.getKeys(deep);
    }
    
    public List<?> getList(String path) {
        return yamlConfiguration.getList(path);
    }
    
    public Location getLocation(String path) {
        //TODO
        //return yamlConfiguration.getLocation(path);
        return null;
    }
    
    public long getLong(String path) {
        return yamlConfiguration.getLong(path);
    }
    
    public List<Long> getLongList(String path) {
        return yamlConfiguration.getLongList(path);
    }
    
    public List<Map<?, ?>> getMapList(String path) {
        return yamlConfiguration.getMapList(path);
    }
    
    public String getName() {
        return yamlConfiguration.getName();
    }
    
//    public <T> T getObject(String path, Class<T> clazz) {
//        return yamlConfiguration.get(path, clazz);
//    }
    
    public OfflinePlayer getOfflinePlayer(String path) {
        return yamlConfiguration.getOfflinePlayer(path);
    }
    
//    public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz) {
//        return yamlConfiguration.getSerializable(path, clazz);
//    }
    
    public List<Short> getShortList(String path) {
        return yamlConfiguration.getShortList(path);
    }
    
    public String getString(String path) {
        return yamlConfiguration.getString(path);
    }
    
    public List<String> getStringList(String path) {
        return yamlConfiguration.getStringList(path);
    }
    
    public Map<String, Object> getValues(boolean deep) {
        return yamlConfiguration.getValues(deep);
    }
    
    public Vector getVector(String path) {
        return yamlConfiguration.getVector(path);
    }
    
    public boolean isBoolean(String path) {
        return yamlConfiguration.isBoolean(path);
    }
    
    public boolean isConfigurationSection(String path) {
        return yamlConfiguration.isConfigurationSection(path);
    }
    
    public boolean isDouble(String path) {
        return yamlConfiguration.isDouble(path);
    }
    
    public boolean isInt(String path) {
        return yamlConfiguration.isInt(path);
    }
    
    public boolean isItemStack(String path) {
        return yamlConfiguration.isItemStack(path);
    }
    
    public boolean isList(String path) {
        return yamlConfiguration.isList(path);
    }
    
//    public boolean isLocation(String path) {
//        return yamlConfiguration.isLocation(path);
//    }
    
    public boolean isLong(String path) {
        return yamlConfiguration.isLong(path);
    }
    
    public boolean isOfflinePlayer(String path) {
        return yamlConfiguration.isOfflinePlayer(path);
    }
    
    public boolean isSet(String path) {
        return yamlConfiguration.isSet(path);
    }
    
    public boolean isString(String path) {
        return yamlConfiguration.isString(path);
    }
    
    public boolean isVector(String path) {
        return yamlConfiguration.isVector(path);
    }
    
    public void set(String path, Object object) {
        yamlConfiguration.set(path, object);
    }
    
    /**
     * Gets the YamlConfiguration instance. This is temporary until passthrough methods are implemented
     * @return The YamlConfiguration instance
     */
    @Deprecated
    public YamlConfiguration getConfiguration() {
        return yamlConfiguration;
    }
    
    /**
     * Sets up this config
     */
    public void setup() {
        if (StringHelper.isEmpty(folder)) {
            this.file = new File(plugin.getDataFolder(), name);
        } else {
            this.file = FileSystems.getDefault().getPath(plugin.getDataFolder().toPath().toString(), folder, name).toFile();
        }
        FileHelper.createFileIfNotExists(file.toPath());
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }
    
    /**
     * Saves this config
     */
    public void save() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save " + name);
        }
    }
    
    public void reload() {
        setup();
    }
}
