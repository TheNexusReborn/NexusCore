package com.thenexusreborn.nexuscore.menu.element;

import com.thenexusreborn.nexuscore.menu.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An element to allow inserting of items. This is an abstract class, so it must be extended
 */
public abstract class InsertElement extends Element {
    
    protected boolean keepOnPageMove = false;
    
    public InsertElement() {
        this(-1);
    }

    public InsertElement(int staticIndex) {
        super(null, staticIndex);
        allowInsert = true;
    }

    public InsertElement(int staticIndex, boolean keepOnPageMove) {
        super(null, staticIndex);
        allowInsert = true;
        this.keepOnPageMove = keepOnPageMove;
    }
    
    public boolean keepOnPageMove() {
        return keepOnPageMove;
    }
    
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public abstract void onInsert(Player player, Menu menu, ItemStack itemStack);
    public void onRemove(Player player, Menu menu) {
        
    }
}
