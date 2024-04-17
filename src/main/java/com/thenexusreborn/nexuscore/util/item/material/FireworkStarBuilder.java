package com.thenexusreborn.nexuscore.util.item.material;

import com.cryptomorin.xseries.XMaterial;
import com.thenexusreborn.nexuscore.util.item.ItemBuilder;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import java.util.List;

public class FireworkStarBuilder extends ItemBuilder {
    
    private FireworkEffect effect;
    
    public FireworkStarBuilder() {
        super(XMaterial.FIREWORK_STAR);
    }

    protected static FireworkStarBuilder createFromItemStack(ItemStack itemStack) {
        FireworkStarBuilder itemBuilder = new FireworkStarBuilder();
        FireworkEffectMeta meta = (FireworkEffectMeta) itemStack.getItemMeta();
        itemBuilder.effect(meta.getEffect());
        return itemBuilder;
    }

    protected static FireworkStarBuilder createFromConfig(ConfigurationSection section) {
        FireworkStarBuilder builder = new FireworkStarBuilder();
        builder.effect((FireworkEffect) section.get("effect", FireworkEffect.class));
        return builder;
    }

    @Override
    public void saveToConfig(ConfigurationSection section) {
        super.saveToConfig(section);
        section.set("effect", this.effect);
    }
    
    public FireworkStarBuilder effect(FireworkEffect effect) {
        this.effect = effect;
        return this;
    }
    
    @Override
    public FireworkStarBuilder addEnchant(Enchantment enchantment, int level) {
        super.addEnchant(enchantment, level);
        return this;
    }

    @Override
    public FireworkStarBuilder addItemFlags(ItemFlag... itemFlags) {
        super.addItemFlags(itemFlags);
        return this;
    }

    @Override
    public FireworkStarBuilder setLore(List<String> lore) {
        super.setLore(lore);
        return this;
    }

    @Override
    public FireworkStarBuilder addLoreLine(String line) {
        super.addLoreLine(line);
        return this;
    }

    @Override
    public FireworkStarBuilder material(XMaterial material) {
        super.material(material);
        return this;
    }

    @Override
    public FireworkStarBuilder amount(int amount) {
        super.amount(amount);
        return this;
    }

    @Override
    public FireworkStarBuilder displayName(String displayName) {
        super.displayName(displayName);
        return this;
    }

    @Override
    public FireworkStarBuilder unbreakable(boolean unbreakable) {
        super.unbreakable(unbreakable);
        return this;
    }

    @Override
    protected FireworkEffectMeta createItemMeta() {
        FireworkEffectMeta itemMeta = (FireworkEffectMeta) super.createItemMeta();
        itemMeta.setEffect(this.effect);
        return itemMeta;
    }

    @Override
    public FireworkStarBuilder clone() {
        FireworkStarBuilder clone = (FireworkStarBuilder) super.clone();
        clone.effect = this.effect;
        return clone;
    }
}
