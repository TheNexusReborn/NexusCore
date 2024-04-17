package com.thenexusreborn.nexuscore.util.item.material;

import com.cryptomorin.xseries.XMaterial;
import com.thenexusreborn.nexuscore.util.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class SkullItemBuilder extends ItemBuilder {
    
    private String owner;
    
    public SkullItemBuilder() {
        super(XMaterial.PLAYER_HEAD);
    }
    
    protected static SkullItemBuilder createFromItemStack(ItemStack itemStack) {
        SkullItemBuilder builder = new SkullItemBuilder();
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        builder.owner(meta.getOwner());
        return builder;
    }

    protected static SkullItemBuilder createFromConfig(ConfigurationSection section) {
        SkullItemBuilder builder = new SkullItemBuilder();
        if (section.contains("owner")) {
            builder.owner(section.getString("owner"));
        }
        
        return builder;
    }

    @Override
    public void saveToConfig(ConfigurationSection section) {
        super.saveToConfig(section);
        if (owner != null) {
            section.set("owner", owner);
        }
    }
    
    public SkullItemBuilder owner(String owner) {
        this.owner = owner;
        return this;
    }
    
    @Override
    public SkullItemBuilder addEnchant(Enchantment enchantment, int level) {
        super.addEnchant(enchantment, level);
        return this;
    }

    @Override
    public SkullItemBuilder addItemFlags(ItemFlag... itemFlags) {
        super.addItemFlags(itemFlags);
        return this;
    }

    @Override
    public SkullItemBuilder setLore(List<String> lore) {
        super.setLore(lore);
        return this;
    }

    @Override
    public SkullItemBuilder addLoreLine(String line) {
        super.addLoreLine(line);
        return this;
    }

    @Override
    public SkullItemBuilder material(XMaterial material) {
        super.material(material);
        return this;
    }

    @Override
    public SkullItemBuilder amount(int amount) {
        super.amount(amount);
        return this;
    }

    @Override
    public SkullItemBuilder displayName(String displayName) {
        super.displayName(displayName);
        return this;
    }

    @Override
    public SkullItemBuilder unbreakable(boolean unbreakable) {
        super.unbreakable(unbreakable);
        return this;
    }

    @Override
    protected SkullMeta createItemMeta() {
        SkullMeta itemMeta = (SkullMeta) super.createItemMeta();
        if (this.owner != null) {
            itemMeta.setOwner(this.owner);
        }
        
        return itemMeta;
    }

    @Override
    public SkullItemBuilder clone() {
        SkullItemBuilder clone = (SkullItemBuilder) super.clone();
        clone.owner = this.owner;
        return clone;
    }
}
