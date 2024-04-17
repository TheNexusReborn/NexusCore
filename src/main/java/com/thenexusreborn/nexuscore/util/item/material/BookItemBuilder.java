package com.thenexusreborn.nexuscore.util.item.material;

import com.cryptomorin.xseries.XMaterial;
import com.stardevllc.starcore.utils.color.ColorUtils;
import com.thenexusreborn.nexuscore.util.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.LinkedList;
import java.util.List;

public class BookItemBuilder extends ItemBuilder {
    
    private String author;
    private List<String> pages = new LinkedList<>();
    private String title;
    
    public BookItemBuilder(XMaterial material) {
        super(material);
    }
    
    protected BookItemBuilder() {
        
    }

    protected static BookItemBuilder createFromItemStack(ItemStack itemStack) {
        BookItemBuilder itemBuilder = new BookItemBuilder();
        BookMeta itemMeta = (BookMeta) itemStack.getItemMeta();
        itemBuilder.setPages(itemMeta.getPages()).author(itemMeta.getAuthor()).title(itemMeta.getTitle());
        return itemBuilder;
    }

    protected static BookItemBuilder createFromConfig(ConfigurationSection section) {
        BookItemBuilder builder = new BookItemBuilder();
        builder.title(section.getString("title"));
        builder.author(section.getString("author"));
        ConfigurationSection pagesSection = section.getConfigurationSection("pages");
        if (pagesSection != null) {
            for (String key : pagesSection.getKeys(false)) {
                builder.addPage(pagesSection.getString(key));
            }
        }
        return builder;
    }

    @Override
    public void saveToConfig(ConfigurationSection section) {
        super.saveToConfig(section);
        section.set("author", this.author);
        section.set("title", this.title);
        for (int i = 0; i < pages.size(); i++) {
            section.set("pages." + i, pages.get(i));
        }
    }
    
    public BookItemBuilder addPage(String page) {
        this.pages.add((ColorUtils.color(page)));
        return this;
    }
    
    public BookItemBuilder setPages(List<String> pages) {
        this.pages.clear();
        pages.forEach(page -> this.pages.add(ColorUtils.color(page)));
        return this;
    }
    
    public BookItemBuilder author(String author) {
        this.author = author;
        return this;
    }
    
    public BookItemBuilder title(String title) {
        this.title = title;
        return this;
    }
    
    @Override
    public BookItemBuilder addEnchant(Enchantment enchantment, int level) {
        super.addEnchant(enchantment, level);
        return this;
    }

    @Override
    public BookItemBuilder addItemFlags(ItemFlag... itemFlags) {
        super.addItemFlags(itemFlags);
        return this;
    }

    @Override
    public BookItemBuilder setLore(List<String> lore) {
        super.setLore(lore);
        return this;
    }

    @Override
    public BookItemBuilder addLoreLine(String line) {
        super.addLoreLine(line);
        return this;
    }

    @Override
    public BookItemBuilder material(XMaterial material) {
        super.material(material);
        return this;
    }

    @Override
    public BookItemBuilder amount(int amount) {
        super.amount(amount);
        return this;
    }

    @Override
    public BookItemBuilder displayName(String displayName) {
        super.displayName(displayName);
        return this;
    }

    @Override
    public BookItemBuilder unbreakable(boolean unbreakable) {
        super.unbreakable(unbreakable);
        return this;
    }

    @Override
    protected BookMeta createItemMeta() {
        BookMeta itemMeta = (BookMeta) super.createItemMeta();
        itemMeta.setTitle(ColorUtils.color(this.title));
        itemMeta.setAuthor(ColorUtils.color(this.author));
        return itemMeta;
    }

    @Override
    public BookItemBuilder clone() {
        BookItemBuilder clone = (BookItemBuilder) super.clone();
        clone.title = this.title;
        clone.author = this.author;
        clone.pages.addAll(this.pages);
        return clone;
    }
}
