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
    private short data;
    private Material material;
    
    public ItemBuilder(Material material) {
        this.material = material;
    }
    
    public ItemBuilder() {}
    
    public ItemBuilder(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }
    
    public static ItemBuilder start(Material material) {
        return new ItemBuilder(material);
    }
    
    public static ItemBuilder start(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }
    
    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount, data);
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
        
        itemMeta.spigot().setUnbreakable(unbreakable);
        
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
    
    public ItemBuilder data(short data) {
        this.data = data;
        return this;
    }
    
    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }
    
    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }
    
    public ItemBuilder addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }
    
    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }
    
    public ItemBuilder storedEnchantments(Map<Enchantment, Integer> storedEnchantments) {
        this.storedEnchantments = storedEnchantments;
        return this;
    }
    
    public ItemBuilder addStoredEnchantment(Enchantment enchantment, int level) {
        this.storedEnchantments.put(enchantment, level);
        return this;
    }
    
    public ItemBuilder itemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }
    
    public ItemBuilder addItemFlag(ItemFlag... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }
    
    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }
    
    public ItemBuilder bannerPatterns(List<Pattern> bannerPatterns) {
        this.bannerPatterns = bannerPatterns;
        return this;
    }
    
    public ItemBuilder blockState(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }
    
    public ItemBuilder bookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
        return this;
    }
    
    public ItemBuilder bookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
        return this;
    }
    
    public ItemBuilder bookPages(List<String> bookPages) {
        this.bookPages = bookPages;
        return this;
    }
    
    public ItemBuilder armorColor(Color armorColor) {
        this.armorColor = armorColor;
        return this;
    }
    
    public ItemBuilder mainEffect(PotionEffectType mainEffect) {
        this.mainEffect = mainEffect;
        return this;
    }
    
    public ItemBuilder potionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
        return this;
    }
    
    public ItemBuilder addPotionEffects(PotionEffect... potionEffects) {
        this.potionEffects.addAll(Arrays.asList(potionEffects));
        return this;
    }
    
    public ItemBuilder repairCost(int repairCost) {
        this.repairCost = repairCost;
        return this;
    }
    
    public ItemBuilder skullOwner(UUID skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }
    
    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemBuilder durability(int durability) {
        this.durability = durability;
        return this;
    }
    
    public ItemBuilder lore(String... lore) {
        if (lore != null) {
            for (String s : lore) {
                addLoreLine(s);
            }
        }
        
        return this;
    }
}