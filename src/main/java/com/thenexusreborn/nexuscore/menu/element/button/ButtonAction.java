package com.thenexusreborn.nexuscore.menu.element.button;

import com.thenexusreborn.nexuscore.menu.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * A functional interface for the action of a button
 */
public interface ButtonAction {
    /**
     * Called when a player click son a button
     * @param player The player that clicked
     * @param menu The menu that this button was a part of
     * @param click The type of Click from the InventoryClickEvent
     */
    void onClick(Player player, Menu menu, ClickType click);
}
