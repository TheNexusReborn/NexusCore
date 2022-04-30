package com.thenexusreborn.nexuscore.menu.element.button;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import org.bukkit.*;

/**
 * Provided class for easier creation of the previous page functionality
 */
public class PreviousPageButton extends Button {
    
    /**
     * Constructs a new PreviousPageButton
     * @param material The material of the item
     * @param color The color for the display name
     */
    public PreviousPageButton(Material material, String color) {
        this(material, color, -1);
    }
    
    /**
     * Constructs a new PreviousPageButton
     * @param material The material of the item
     * @param color The color of the displayname
     * @param staticIndex The static index - Used internally
     */
    public PreviousPageButton(Material material, String color, int staticIndex) {
        super(ItemBuilder.start(material).displayName(color + "Previous Page").build(), staticIndex);
        this.leftClickAction = (player, menu, click) -> {
            int currentPage = menu.getCurrentPage();

            if (currentPage == 1) {
                player.sendMessage(MCUtils.color("&cYou are already at the first page."));
                return;
            }

            menu.setCurrentPage(--currentPage);
            Bukkit.getScheduler().runTaskLater(NexusCore.getPlugin(NexusCore.class), () -> player.openInventory(menu.getInventory()), 1L);
        };
    }
}
