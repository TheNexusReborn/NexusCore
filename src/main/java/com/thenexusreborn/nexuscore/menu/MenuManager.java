package com.thenexusreborn.nexuscore.menu;

import com.thenexusreborn.nexuscore.menu.element.*;
import com.thenexusreborn.nexuscore.menu.element.button.Button;
import com.thenexusreborn.nexuscore.menu.gui.Menu;
import com.thenexusreborn.nexuscore.menu.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Main class of the library
 * The MenuManager class is registered as a Bukkit Service and registers the events needed as well as the default menu provider
 */
public class MenuManager implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (!(e.getClickedInventory().getHolder() instanceof Menu))
            return;
        Menu menu = (Menu) e.getClickedInventory().getHolder();
        if (e.getSlot() != e.getRawSlot())
            return;
        Slot slot = menu.getSlot(e.getSlot());
        if (slot == null)
            return;
        Element element = slot.getElement();
        if (element == null || !slot.getElement().isAllowInsert()) {
            e.setCancelled(true);
        }
        
        if (element instanceof Button) {
            Button button = (Button) element; 
            button.playSound(player);
            if (e.isLeftClick()) {
                if (button.getLeftClickAction() != null) {
                    button.getLeftClickAction().onClick(player, menu, e.getClick());
                }
            } else if (e.isRightClick()) {
                if (button.getRightClickAction() != null) {
                    button.getRightClickAction().onClick(player, menu, e.getClick());
                }
            }
        } else if (element instanceof InsertElement) {
            InsertElement insertElement = (InsertElement) element;
            if (e.getAction().name().toLowerCase().contains("pickup_")) {
                insertElement.onRemove(player, menu);
            } else if (e.getAction().name().toLowerCase().contains("place_")) {
                if (insertElement.keepOnPageMove()) {
                    insertElement.setItemStack(e.getCursor());
                }
                insertElement.onInsert(player, menu, e.getCursor());
            }
        }
    }
}
