package com.thenexusreborn.nexuscore.menu.element.button;

import com.thenexusreborn.nexuscore.NexusCore;
import com.thenexusreborn.nexuscore.util.MCUtils;
import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import org.bukkit.*;

/**
 * Provided class for easier creation of the next page functionality
 */
public class NextPageButton extends Button {
    
    /**
     * Constructs a new NextPageButton
     * @param material The material of the item
     * @param color The color for the display name
     */
    public NextPageButton(Material material, String color) {
        this(material, color, -1);
    }
    
    /**
     * Constructs a new NextPageButton
     * @param material The material of the item
     * @param color The color of the displayname
     * @param staticIndex The static index - Used internally
     */
    public NextPageButton(Material material, String color, int staticIndex) {
        super(ItemBuilder.start(material).displayName(color + "Next Page").build(), staticIndex);
        this.leftClickAction = (player, menu, click) -> {
            int totalPages = menu.getTotalPages();
            int currentPage = menu.getCurrentPage();

            if (currentPage == totalPages) {
                player.sendMessage(MCUtils.color("&cYou are already at the last page."));
                return;
            }

            menu.setCurrentPage(++currentPage);
            Bukkit.getScheduler().runTaskLater(NexusCore.getPlugin(NexusCore.class), () -> player.openInventory(menu.getInventory()), 1L);
        };
    }
}
