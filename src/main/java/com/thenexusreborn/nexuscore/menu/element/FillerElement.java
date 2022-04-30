package com.thenexusreborn.nexuscore.menu.element;

import com.thenexusreborn.nexuscore.util.builder.ItemBuilder;
import org.bukkit.Material;

/**
 * Represents an element that fills in spaces in a Menu
 * Please do not use this class directly and use the Menu.setFillerRange() or Menu.setFillerSlots methods
 */
public class FillerElement extends Element {
    public FillerElement(Material material, int staticIndex) {
        super(ItemBuilder.start(material).displayName("&f").build(), staticIndex);
        this.isReplaceable = true;
    }
}
