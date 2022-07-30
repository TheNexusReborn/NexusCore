package com.thenexusreborn.nexuscore.util.builder;

import com.thenexusreborn.nexuscore.util.MCUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;

import java.util.*;

/**
 * A class to make creating items better
 * <p> 
 * This does check to see if the settings and values are valid for the item type and is safe to use.
 */
public class ItemBuilder {
    private String displayName;
    private List<String> lore = new LinkedList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>(), storedEnchantments = new HashMap<>();
    private Set<ItemFlag> itemFlags = new HashSet<>();
    private boolean unbreakable;
    private List<Pattern> bannerPatterns = new ArrayList<>();
    private BlockState blockState;
    private String bookTitle, bookAuthor;
    private List<String> bookPages = new LinkedList<>();
    private Color armorColor;
    private PotionEffectType mainEffect;
    private List<PotionEffect> potionEffects = new ArrayList<>();
    private int repairCost = -1;
    private UUID skullOwner;
    private int amount = 1, durability;
    private Material material;
    
    /**
     * Constructs a new ItemBuilder using a Material
     * @param material The starting Material
     */
    public ItemBuilder(Material material) {
        this.material = material;
    }
    
    /**
     * Constructs an ItemBuilder that is empty
     */
    public ItemBuilder() {}
    
    /**
     * Constructs an ItemBuilder with a starting material and amount
     * @param material The material
     * @param amount The amount
     */
    public ItemBuilder(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }
    
    /**
     * The static method of the constructor
     * @param material The material
     * @return The created ItemBuilder
     */
    public static ItemBuilder start(Material material) {
        return new ItemBuilder(material);
    }
    
    /**
     * The static method of the con structor
     * @param material The material
     * @param amount The amount
     * @return The created ItemBuilder
     */
    public static ItemBuilder start(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }
    
    /**
     * Builds the ItemStack based on the builder
     * @return The ItemStack
     */
    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (displayName != null) {
            itemMeta.setDisplayName(MCUtils.color(displayName));
        }
        
        if (!lore.isEmpty()) {
            List<String> coloredLore = new LinkedList<>();
            lore.forEach(line -> coloredLore.add(MCUtils.color(line)));
            itemMeta.setLore(coloredLore);
        }
        
        if (!enchantments.isEmpty()) {
            enchantments.forEach((enchant, level) -> itemMeta.addEnchant(enchant, level, true));
        }
        
//        if (!storedEnchantments.isEmpty()) {
//            if (itemMeta instanceof EnchantmentStorageMeta enchantStorage) {
//                storedEnchantments.forEach((enchant, level) -> enchantStorage.addStoredEnchant(enchant, level, true));
//            }
//        }
        
        if (!itemFlags.isEmpty()) {
            itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }
        
        //TODO itemMeta.setUnbreakable(unbreakable);
        
//        if (itemMeta instanceof BannerMeta bannerMeta) {
////            if (bannerColor != null)
////                bannerMeta.setBaseColor(bannerColor);
//            bannerMeta.setPatterns(bannerPatterns);
//        }
        
        if (itemMeta instanceof BlockStateMeta) {
            if (blockState != null) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
                blockStateMeta.setBlockState(blockState);
            }
        }
        
//        if (itemMeta instanceof Damageable damageable) {
//            damageable.setDamage(durability);
//        }
        
//        if (itemMeta instanceof BookMeta bookMeta) {
//            if (bookTitle != null) {
//                bookMeta.setTitle(MCUtils.color(bookTitle));
//            }
//            if (bookAuthor != null) {
//                bookMeta.setTitle(MCUtils.color(bookAuthor));
//            }
//            
//            if (!bookPages.isEmpty()) {
//                bookMeta.setPages(bookPages);
//            }
//        }
        
        if (itemMeta instanceof LeatherArmorMeta) {
            if (armorColor != null) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
                leatherArmorMeta.setColor(armorColor);
            }
        }
        
//        if (itemMeta instanceof PotionMeta) {
//            if (mainEffect != null) {
//                PotionMeta potionMeta = (PotionMeta) itemMeta;
//                potionMeta.setBasePotionData(new PotionData(null)); //TODO
//                if (!potionEffects.isEmpty()) {
//                    potionEffects.forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
//                }
//            }
//        }
        
        if (itemMeta instanceof Repairable) {
            if (repairCost != -1) {
                Repairable repairable = (Repairable) itemMeta;
                repairable.setRepairCost(repairCost);
            }
        }
        
//        if (itemMeta instanceof SkullMeta) {
//            if (this.skullOwner != null) {
//                SkullMeta skullMeta = (SkullMeta) itemMeta;
//                OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(this.skullOwner);
//                if (skullPlayer != null) {
//                    skullMeta.setOwnerProfile(skullPlayer.getPlayerProfile());
//                    skullMeta.setOwningPlayer(skullPlayer);
//                }
//            }
//        }
        
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    /** 
     * Sets the material
     * @param material The material
     * @return This ItemBuilder
     */
    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }
    
    /**
     * Sets the DisplayName
     * @param displayName The new display name
     * @return This ItemBuilder
     */
    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    /**
     * Sets the lore
     * @param lore The lore
     * @return This ItemBuilder
     */
    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }
    
    /**
     * Adds a line to the exisiting lore
     * @param line The line to add
     * @return This ItemBuilder
     */
    public ItemBuilder addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }
    
    /**
     * Sets the current enchantments
     * @param enchantments The enchantments to set
     * @return This ItemBuilder
     */
    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }
    
    /**
     * Adds an enchantment to the current list of enchantments
     * @param enchantment The enchantment
     * @param level The level of the enchantment
     * @return This ItemBuilder
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }
    
    /**
     * Sets the stored enchantments, this is for items that store enchantments like Enchanted Books
     * @param storedEnchantments The enchantments
     * @return This ItemBuilder
     */
    public ItemBuilder storedEnchantments(Map<Enchantment, Integer> storedEnchantments) {
        this.storedEnchantments = storedEnchantments;
        return this;
    }
    
    /**
     * Adds a stored enchantment
     * @param enchantment The enchantment
     * @param level The level of the Enchantment
     * @return This ItemBuilder
     */
    public ItemBuilder addStoredEnchantment(Enchantment enchantment, int level) {
        this.storedEnchantments.put(enchantment, level);
        return this;
    }
    
    /**
     * Sets the ItemFlags
     * @param itemFlags The ItemFlags
     * @return This ItemBuilder
     */
    public ItemBuilder itemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }
    
    /**
     * Adds an ItemFlag
     * @param flags The ItemFlag(s) vararg
     * @return This ItemBuilder
     */
    public ItemBuilder addItemFlag(ItemFlag... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }
    
    /**
     * Sets the unbreakable value 
     * @param unbreakable The value
     * @return This ItemBuilder
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }
    
    /**
     * Sets the Banner patterns
     * @param bannerPatterns The banner patterns
     * @return This ItemBuilder
     */
    public ItemBuilder bannerPatterns(List<Pattern> bannerPatterns) {
        this.bannerPatterns = bannerPatterns;
        return this;
    }
    
    /**
     * Sets the BlockState (If it is a block)
     * @param blockState The state to set
     * @return This ItemBuilder
     */
    public ItemBuilder blockState(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }
    
    /**
     * Sets the book title
     * @param bookTitle The title of the book
     * @return This ItemBuilder
     */
    public ItemBuilder bookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
        return this;
    }
    
    /**
     * Sets the Author of the book
     * @param bookAuthor The author
     * @return This ItemBuilder
     */
    public ItemBuilder bookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
        return this;
    }
    
    /**
     * Sets the pages of the book
     * @param bookPages The pages
     * @return This ItemBuilder
     */
    public ItemBuilder bookPages(List<String> bookPages) {
        this.bookPages = bookPages;
        return this;
    }
    
    /**
     * Sets the color of the armor
     * @param armorColor The color
     * @return This ItemBuilder
     */
    public ItemBuilder armorColor(Color armorColor) {
        this.armorColor = armorColor;
        return this;
    }
    
    /**
     * Sets the Main Potion Effect Type
     * @param mainEffect The effect
     * @return This ItemBuilder
     */
    public ItemBuilder mainEffect(PotionEffectType mainEffect) {
        this.mainEffect = mainEffect;
        return this;
    }
    
    /**
     * Sets the potion effects
     * @param potionEffects The potion effects
     * @return This ItemBuilder
     */
    public ItemBuilder potionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
        return this;
    }
    
    /**
     * Adds a PotionEffect
     * @param potionEffects The potion effect varargs
     * @return This ItemBuilder
     */
    public ItemBuilder addPotionEffects(PotionEffect... potionEffects) {
        this.potionEffects.addAll(Arrays.asList(potionEffects));
        return this;
    }
    
    /**
     * Sets the repair cost
     * @param repairCost The new repair cost
     * @return This ItemBuilder
     */
    public ItemBuilder repairCost(int repairCost) {
        this.repairCost = repairCost;
        return this;
    }
    
    /**
     * Sets the Skull Owner
     * @param skullOwner The UUID of the player
     * @return This ItemBuilder
     */
    public ItemBuilder skullOwner(UUID skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }
    
    /**
     * Sets the Amount of the itemstack
     * @param amount The amount
     * @return This ItemBuilder
     */
    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }
    
    /**
     * Sets the durability of the item
     * @param durability The durability
     * @return This ItemBuilder
     */
    public ItemBuilder durability(int durability) {
        this.durability = durability;
        return this;
    }
    
    /**
     * Sets the lore in a varargs way
     * @param lore The lore to set (varargs)
     * @return This ItemBuilder
     */
    public ItemBuilder lore(String... lore) {
        if (lore != null) {
            for (String s : lore) {
                addLoreLine(s);
            }
        }
        
        return this;
    }
}