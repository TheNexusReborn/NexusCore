package com.thenexusreborn.nexuscore.util.item.material;

import com.cryptomorin.xseries.XMaterial;
import com.thenexusreborn.nexuscore.util.item.ItemBuilder;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FireworkItemBuilder extends ItemBuilder {
    
    private List<FireworkEffect> effects = new LinkedList<>();
    private int power;
    
    public FireworkItemBuilder() {
        super(XMaterial.FIREWORK_ROCKET);
    }
    
    protected static FireworkItemBuilder createFromItemStack(ItemStack itemStack) {
        FireworkItemBuilder itemBuilder = new FireworkItemBuilder();
        FireworkMeta meta = (FireworkMeta) itemStack.getItemMeta();
        itemBuilder.power(meta.getPower()).setEffects(meta.getEffects());
        return itemBuilder;
    }

    protected static FireworkItemBuilder createFromConfig(ConfigurationSection section) {
        FireworkItemBuilder builder = new FireworkItemBuilder();
        builder.power(section.getInt("power"));
        ConfigurationSection effectsSection = section.getConfigurationSection("effects");
        if (effectsSection != null) {
            for (String key : effectsSection.getKeys(false)) {
                FireworkEffect object = (FireworkEffect) effectsSection.get(key);
                builder.addEffect(object);
            }
        }
        return builder;
    }

    @Override
    public void saveToConfig(ConfigurationSection section) {
        super.saveToConfig(section);
        section.set("power", this.power);
        for (int i = 0; i < effects.size(); i++) {
            section.set("effects." + i, effects.get(i));
        }
    }
    
    public FireworkItemBuilder power(int power) {
        this.power = power;
        return this;
    }
    
    public FireworkItemBuilder addEffect(FireworkEffect effect) {
        this.effects.add(effect);
        return this;
    }
    
    public FireworkItemBuilder addEffects(Collection<FireworkEffect> effects) {
        this.effects.addAll(effects);
        return this;
    }
    
    public FireworkItemBuilder setEffects(Collection<FireworkEffect> effects) {
        this.effects.clear();
        this.effects.addAll(effects);
        return this;
    }

    @Override
    public FireworkItemBuilder addEnchant(Enchantment enchantment, int level) {
        super.addEnchant(enchantment, level);
        return this;
    }

    @Override
    public FireworkItemBuilder addItemFlags(ItemFlag... itemFlags) {
        super.addItemFlags(itemFlags);
        return this;
    }

    @Override
    public FireworkItemBuilder setLore(List<String> lore) {
        super.setLore(lore);
        return this;
    }

    @Override
    public FireworkItemBuilder addLoreLine(String line) {
        super.addLoreLine(line);
        return this;
    }

    @Override
    public FireworkItemBuilder material(XMaterial material) {
        super.material(material);
        return this;
    }

    @Override
    public FireworkItemBuilder amount(int amount) {
        super.amount(amount);
        return this;
    }

    @Override
    public FireworkItemBuilder displayName(String displayName) {
        super.displayName(displayName);
        return this;
    }

    @Override
    public FireworkItemBuilder unbreakable(boolean unbreakable) {
        super.unbreakable(unbreakable);
        return this;
    }

    @Override
    protected FireworkMeta createItemMeta() {
        FireworkMeta itemMeta = (FireworkMeta) super.createItemMeta();
        itemMeta.addEffects(this.effects);
        itemMeta.setPower(this.power);
        return itemMeta;
    }

    @Override
    public FireworkItemBuilder clone() {
        FireworkItemBuilder clone = (FireworkItemBuilder) super.clone();
        clone.power = this.power;
        clone.effects.addAll(this.effects);
        return clone;
    }
}
