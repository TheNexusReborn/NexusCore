package com.thenexusreborn.nexuscore.util.item;

import com.cryptomorin.xseries.XMaterial;
import com.stardevllc.starcore.utils.color.ColorUtils;
import com.stardevllc.starcore.utils.item.material.MapItemBuilder;
import com.thenexusreborn.nexuscore.util.item.enums.BookType;
import com.thenexusreborn.nexuscore.util.item.enums.ToolMaterial;
import com.thenexusreborn.nexuscore.util.item.enums.ToolType;
import com.thenexusreborn.nexuscore.util.item.material.*;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.map.MapView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A builder for Items <br>
 * You can use the child classes to customize every type that has an ItemMeta <br>
 * Note: This uses the XMaterial library to allow multi-version support
 */
@SuppressWarnings("deprecation")
public class ItemBuilder implements Cloneable {

    private static final Map<Class<? extends ItemMeta>, Class<? extends ItemBuilder>> META_TO_BUILDERS = new HashMap<>();

    static {
        META_TO_BUILDERS.put(BannerMeta.class, BannerItemBuilder.class);
        META_TO_BUILDERS.put(BookMeta.class, BookItemBuilder.class);
        META_TO_BUILDERS.put(EnchantmentStorageMeta.class, EnchantedBookBuilder.class);
        META_TO_BUILDERS.put(FireworkMeta.class, FireworkItemBuilder.class);
        META_TO_BUILDERS.put(FireworkEffectMeta.class, FireworkStarBuilder.class);
        META_TO_BUILDERS.put(SkullMeta.class, SkullItemBuilder.class);
    }

    protected XMaterial material;
    protected int amount;
    protected Map<Enchantment, Integer> enchantments = new HashMap<>();
    protected ItemFlag[] itemFlags;
    protected String displayName;
    protected List<String> lore = new LinkedList<>();
    protected boolean unbreakable;
    protected int repairCost;
    protected int damage;

    @SuppressWarnings("SuspiciousMethodCalls")
    public static ItemBuilder fromConfig(ConfigurationSection section) {
        XMaterial material = XMaterial.valueOf(section.getString("material"));
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material.parseMaterial());
        ItemBuilder itemBuilder;
        if (META_TO_BUILDERS.containsKey(itemMeta)) {
            itemBuilder = getSubClassFromMeta(itemMeta, "createFromConfig", ConfigurationSection.class, section);
        } else {
            itemBuilder = new ItemBuilder();
        }

        itemBuilder.material(material);
        itemBuilder.amount(section.getInt("amount"));
        itemBuilder.displayName(section.getString("displayname"));
        itemBuilder.setLore(section.getStringList("lore"));
        itemBuilder.repairCost(section.getInt("repaircost"));
        itemBuilder.damage(section.getInt("damage"));
        List<String> flagNames = section.getStringList("flags");
        if (!flagNames.isEmpty()) {
            for (String flagName : flagNames) {
                itemBuilder.addItemFlags(ItemFlag.valueOf(flagName));
            }
        }

        ConfigurationSection enchantsSection = section.getConfigurationSection("enchantments");
        if (enchantsSection != null) {
            for (String enchantName : enchantsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(enchantName);
                int level = enchantsSection.getInt(enchantName);
                itemBuilder.addEnchant(enchantment, level);
            }
        }

        return itemBuilder;
    }

    public void saveToConfig(ConfigurationSection section) {
        section.set("material", material.name());
        section.set("amount", amount);
        section.set("displayname", displayName);
        section.set("lore", lore);
        section.set("repaircost", repairCost);
        section.set("damage", damage);
        List<String> flags = new ArrayList<>();
        if (itemFlags != null) {
            for (ItemFlag itemFlag : itemFlags) {
                flags.add(itemFlag.name());
            }
        }
        section.set("flags", flags);

        enchantments.forEach((enchant, level) -> section.set("enchantments." + enchant.getName(), level));
    }

    public static ItemBuilder of(XMaterial material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder fromItemStack(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemBuilder itemBuilder = getSubClassFromMeta(itemMeta, "createFromItemStack", ItemStack.class, itemStack);

        itemBuilder.displayName(itemMeta.getDisplayName()).amount(itemStack.getAmount()).addItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]))
                .unbreakable(itemMeta.spigot().isUnbreakable()).setLore(itemMeta.getLore()).setEnchants(itemMeta.getEnchants());

        if (itemMeta instanceof Repairable repairable) {
            itemBuilder.repairCost(repairable.getRepairCost());
        }

        itemBuilder.damage(itemStack.getDurability());
        return itemBuilder;
    }

    protected ItemBuilder() {

    }

    protected ItemBuilder(XMaterial material) {
        this.material = material;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder setEnchants(Map<Enchantment, Integer> enchants) {
        this.enchantments.clear();
        this.enchantments.putAll(enchants);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore.clear();
        this.lore.addAll(lore);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }

    public ItemBuilder setLoreLine(int index, String line) {
        this.lore.set(index, line);
        return this;
    }

    public ItemBuilder material(XMaterial material) {
        this.material = material;
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder repairCost(int repairCost) {
        this.repairCost = repairCost;
        return this;
    }

    public ItemBuilder damage(int damage) {
        this.damage = damage;
        return this;
    }

    protected ItemMeta createItemMeta() {
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(this.material.parseMaterial());

        if (!this.enchantments.isEmpty()) {
            this.enchantments.forEach((enchant, level) -> itemMeta.addEnchant(enchant, level, true));
        }

        if (itemFlags != null) {
            itemMeta.addItemFlags(this.itemFlags);
        }

        if (this.displayName != null) {
            itemMeta.setDisplayName(ColorUtils.color(this.displayName));
        }

        if (!this.lore.isEmpty()) {
            List<String> coloredLore = this.lore.stream().map(ColorUtils::color).collect(Collectors.toCollection(LinkedList::new));
            itemMeta.setLore(coloredLore);
        }

        if (itemMeta instanceof Repairable repairable) {
            repairable.setRepairCost(this.repairCost);
        }

        itemMeta.spigot().setUnbreakable(unbreakable);
        return itemMeta;
    }

    public ItemStack build() {
        if (amount < 1) {
            amount = 1;
        }

        ItemStack itemStack = material.parseItem();
        itemStack.setAmount(amount);
        itemStack.setDurability((short) this.damage);
        ItemMeta itemMeta = createItemMeta();
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemBuilder clone() {
        ItemBuilder clone = null;
        try {
            clone = (ItemBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        clone.amount = amount;
        clone.enchantments.putAll(this.enchantments);
        clone.itemFlags = this.itemFlags;
        clone.displayName = this.displayName;
        clone.lore.addAll(this.lore);
        clone.unbreakable = this.unbreakable;
        clone.repairCost = this.repairCost;
        clone.damage = this.damage;
        return clone;
    }

    public static class Banner extends BannerItemBuilder {
        public Banner(DyeColor dyeColor) {
            super(switch (dyeColor) {
                case WHITE -> XMaterial.WHITE_BANNER;
                case ORANGE -> XMaterial.ORANGE_BANNER;
                case MAGENTA -> XMaterial.MAGENTA_BANNER;
                case LIGHT_BLUE -> XMaterial.LIGHT_BLUE_BANNER;
                case YELLOW -> XMaterial.YELLOW_BANNER;
                case LIME -> XMaterial.LIME_BANNER;
                case PINK -> XMaterial.PINK_BANNER;
                case GRAY -> XMaterial.GRAY_BANNER;
                case SILVER -> XMaterial.LIGHT_GRAY_BANNER;
                case CYAN -> XMaterial.CYAN_BANNER;
                case PURPLE -> XMaterial.PURPLE_BANNER;
                case BLUE -> XMaterial.BLUE_BANNER;
                case BROWN -> XMaterial.BROWN_BANNER;
                case GREEN -> XMaterial.GREEN_BANNER;
                case RED -> XMaterial.RED_BANNER;
                case BLACK -> XMaterial.BLACK_BANNER;
            });
        }
    }

    public static class Book extends BookItemBuilder {
        public Book(BookType bookType, String title, String author) {
            super(XMaterial.valueOf(bookType.name() + "_BOOK"));
            title(title).author(author);
        }
    }

    public static class EnchantedBook extends EnchantedBookBuilder {
        public EnchantedBook(Enchantment enchantment, int level) {
            addEnchant(enchantment, level);
        }
    }

    public static class Firework extends FireworkItemBuilder {
        public Firework(FireworkEffect effect, int power) {
            super.addEffect(effect).power(power);
        }
    }

    public static class FireworkStar extends FireworkStarBuilder {
        public FireworkStar(FireworkEffect effect) {
            super.effect(effect);
        }
    }

    public static class MapItem extends MapItemBuilder {
        public MapItem(MapView mapView) {
            super.mapView(mapView);
        }
    }

    public static class Skull extends SkullItemBuilder {
        public Skull(String owner) {
            super.owner(owner);
        }
    }

    public static class Tool extends ItemBuilder {
        public Tool(ToolMaterial material, ToolType type) {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material.name() + "_" + type.name());
            xMaterial.ifPresent(value -> this.material = value);
        }
    }

    private static ItemBuilder getSubClassFromMeta(ItemMeta itemMeta, String methodName, Class<?> paramClass, Object param) {
        Class<? extends ItemBuilder> itemBuilderClass = null;
        for (Map.Entry<Class<? extends ItemMeta>, Class<? extends ItemBuilder>> entry : META_TO_BUILDERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(itemMeta.getClass())) {
                itemBuilderClass = entry.getValue();
                break;
            }
        }

        try {
            Method method = itemBuilderClass.getDeclaredMethod(methodName, paramClass);
            method.setAccessible(true);
            return (ItemBuilder) method.invoke(null, param);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
            Logger logger = Bukkit.getLogger();
            logger.log(Level.SEVERE, "Error while parsing an ItemStack into an ItemBuilder", e);
            return null;
        }
    }
}