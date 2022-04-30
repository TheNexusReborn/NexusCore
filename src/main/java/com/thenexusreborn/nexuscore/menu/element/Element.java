package com.thenexusreborn.nexuscore.menu.element;

import org.bukkit.inventory.ItemStack;

/**
 * This represents and element in a Menu
 */
public class Element {

    protected ItemStack itemStack;
    protected boolean isStatic;
    protected int staticIndex = -1;
    protected boolean allowInsert;
    protected boolean isReplaceable;
    
    /**
     * Constructs a new Element
     * @param itemStack The ItemStack
     */
    public Element(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    /**
     * Constructs a new Element
     * @param itemStack The ItemStack
     * @param staticIndex The static index
     */
    public Element(ItemStack itemStack, int staticIndex) {
        this.itemStack = itemStack;
        this.staticIndex = staticIndex;
        if (staticIndex > -1) {
            this.isStatic = true;
        }
    }
    
    /**
     * Sets the static inded and the isStatic flag
     * @param staticIndex The new index
     */
    public void setStaticIndex(int staticIndex) {
        this.staticIndex = staticIndex;
        this.isStatic = staticIndex > -1;
    }
    
    /**
     * If this element is replaceable with other elements when the Menu is being created
     * @return If this element is replaceable
     */
    public boolean isReplaceable() {
        return isReplaceable;
    }
    
    /**
     * Gets the ItemStack of this element
     * @return The ItemStack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    /**
     * If this element is static
     * Being static means it persists across all pages of a Menu.
     * @return If this is static
     */
    public boolean isStatic() {
        return staticIndex > -1;
    }
    
    /**
     * Gets the Index of the element
     * This cannot exceed the Menu's maximum item count
     * @return The current index
     */
    public int getStaticIndex() {
        return staticIndex;
    }
    
    /**
     * If this element allows inserting of items. Note: There is no base functionality in this class
     * Please create a child class of the InsertElement abstract class
     * @return If this allows inserting of items
     */
    public boolean isAllowInsert() {
        return allowInsert;
    }
    
    /**
     * Sets the ItemStakc
     * @param itemStack The ItemStack
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    /**
     * Sets the allowInsert flag (Not recommended without using the InsertElement class)
     * @param allowInsert The new value
     */
    public void setAllowInsert(boolean allowInsert) {
        this.allowInsert = allowInsert;
    }
    
    /**
     * Sets the replaceable flag (Not recommended without using a child class)
     * @param replaceable The new value
     */
    public void setReplaceable(boolean replaceable) {
        isReplaceable = replaceable;
    }
    
    @Override
    public String toString() {
        return itemStack.getType().toString();
    }
}
