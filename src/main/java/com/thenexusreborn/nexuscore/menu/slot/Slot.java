package com.thenexusreborn.nexuscore.menu.slot;

import com.thenexusreborn.nexuscore.menu.element.Element;

/**
 * A utility class used to make it easier to manage the menus internally
 */
public class Slot {
    
    protected final int index;
    protected Element element;

    public Slot(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
