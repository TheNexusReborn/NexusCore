package com.thenexusreborn.nexuscore.util.item.material;

import com.cryptomorin.xseries.XMaterial;
import com.thenexusreborn.nexuscore.util.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantedBookBuilder extends ItemBuilder {

    protected Map<Enchantment, Integer> storedEnchants = new HashMap<>();
    
    public EnchantedBookBuilder() {
        super(XMaterial.ENCHANTED_BOOK);
    }
    
    protected static EnchantedBookBuilder createFromItemStack(ItemStack itemStack) {
        EnchantedBookBuilder itemBuilder = new EnchantedBookBuilder();
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        itemBuilder.setStoredEnchants(itemMeta.getStoredEnchants());
        return itemBuilder;
    }

    protected static EnchantedBookBuilder createFromConfig(ConfigurationSection section) {
        EnchantedBookBuilder builder = new EnchantedBookBuilder();
        ConfigurationSection storedEnchantsSection = section.getConfigurationSection("storedenchantments");
        if (storedEnchantsSection != null) {
            for (String enchantName : storedEnchantsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(enchantName);
                int level = storedEnchantsSection.getInt(enchantName);
                builder.addStoredEnchant(enchantment, level);
            }
        }
        return builder;
    }

    @Override
    public void saveToConfig(ConfigurationSection section) {
        super.saveToConfig(section);
        storedEnchants.forEach((enchant, level) -> section.set("storedenchantments." + enchant.getName(), level));
    }

    public EnchantedBookBuilder addStoredEnchant(Enchantment enchantment, int level) {
        this.storedEnchants.put(enchantment, level);
        return this;
    }
    
    public EnchantedBookBuilder setStoredEnchants(Map<Enchantment, Integer> enchants) {
        this.storedEnchants.clear();
        this.storedEnchants.putAll(enchants);
        return this;
    }
    
    @Override
    public EnchantedBookBuilder addEnchant(Enchantment enchantment, int level) {
        super.addEnchant(enchantment, level);
        return this;
    }

    @Override
    public EnchantedBookBuilder addItemFlags(ItemFlag... itemFlags) {
        super.addItemFlags(itemFlags);
        return this;
    }

    @Override
    public EnchantedBookBuilder setLore(List<String> lore) {
        super.setLore(lore);
        return this;
    }

    @Override
    public EnchantedBookBuilder addLoreLine(String line) {
        super.addLoreLine(line);
        return this;
    }

    @Override
    public EnchantedBookBuilder material(XMaterial material) {
        super.material(material);
        return this;
    }

    @Override
    public EnchantedBookBuilder amount(int amount) {
        super.amount(amount);
        return this;
    }

    @Override
    public EnchantedBookBuilder displayName(String displayName) {
        super.displayName(displayName);
        return this;
    }

    @Override
    public EnchantedBookBuilder unbreakable(boolean unbreakable) {
        super.unbreakable(unbreakable);
        return this;
    }

    @Override
    protected EnchantmentStorageMeta createItemMeta() {
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) super.createItemMeta();
        this.storedEnchants.forEach((enchant, level) -> itemMeta.addStoredEnchant(enchant, level, true));
        return itemMeta;
    }

    @Override
    public EnchantedBookBuilder clone() {
        EnchantedBookBuilder clone = (EnchantedBookBuilder) super.clone();
        clone.storedEnchants.putAll(this.storedEnchants);
        return clone;
    }
}
