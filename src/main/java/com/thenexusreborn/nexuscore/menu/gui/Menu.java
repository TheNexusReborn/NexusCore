package com.thenexusreborn.nexuscore.menu.gui;

import com.thenexusreborn.api.util.Pair;
import com.thenexusreborn.nexuscore.menu.element.*;
import com.thenexusreborn.nexuscore.menu.slot.Slot;
import com.thenexusreborn.nexuscore.util.*;
import com.thenexusreborn.api.collection.IncrementalMap;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Menu implements InventoryHolder {
    protected final JavaPlugin plugin;
    protected final String name;
    protected final String title;
    protected final int rows;
    protected final IncrementalMap<Element> elements = new IncrementalMap<>();
    protected final Map<Integer, Slot> slots = new TreeMap<>();
    protected int currentPage = 1;
    
    public Menu(JavaPlugin plugin, String name, String title, int rows) {
        this.plugin = plugin;
        this.name = name;
        this.title = title;
        this.rows = rows;
        
        if (rows > 6) {
            throw new IllegalArgumentException("Too many rows for the menu " + name);
        }
        int totalSlots = rows * 9;
        for (int i = 0; i < totalSlots; i++) {
            slots.put(i, new Slot(i));
        }
    }
    
    public void setElement(int row, int column, Element element) {
        int position = (row * 9) + column;
        setElement(position, element);
    }
    
    public void addElements(Element... elements) {
        for (Element element : elements) {
            addElement(element);
        }
    }
    
    public void setFillerRange(Material material, int from, int to) {
        for (int i = from; i <= to; i++) {
            Slot slot = this.slots.get(i);
            if (slot == null) {
                plugin.getLogger().severe("Slot " + i + " from the menu " + this.name + " is null.");
                return;
            }
            if (slot.getElement() == null) {
                FillerElement element = new FillerElement(material, i);
                slot.setElement(element);
                addElement(element);
            }
        }
    }
    
    public void setElement(int position, Element element) {
        Slot slot = getSlot(position);
        if (slot == null) {
            this.slots.put(position, new Slot(position));
            slot = getSlot(position);
        }
        slot.setElement(element);
        element.setStaticIndex(position);
        this.elements.put(position, element);
    }
    
    public void setFillerSlots(Material material, int... slots) {
        for (int s : slots) {
            Slot slot = this.slots.get(s);
            if (slot == null) {
                plugin.getLogger().severe("Slot " + s + " from the menu " + this.name + " is null.");
                return;
            }
            if (slot.getElement() == null) {
                FillerElement element = new FillerElement(material, s);
                setElement(s, element);
            }
        }
    }
    
    public Inventory getInventory() {
        if (currentPage < 1) {
            currentPage = 1;
        }
        int invSize = rows * 9;
        Inventory inv = Bukkit.createInventory(this, invSize, MCUtils.color(title));
    
        Pair<NormalElements, StaticElements> filtered = filterElements();
        StaticElements staticElements = filtered.secondValue();
        NormalElements nonStaticElements = filtered.firstValue();
    
        IncrementalMap<Element> fillerElements = new IncrementalMap<>();
        IncrementalMap<Element> normalStaticElements = new IncrementalMap<>();
        for (Element staticElement : staticElements) {
            if (staticElement instanceof FillerElement) {
                fillerElements.add(staticElement);
            } else {
                normalStaticElements.add(staticElement);
            }
        }

        for (Element element : normalStaticElements.values()) {
            slots.get(element.getStaticIndex()).setElement(element);
        }
    
        for (Element element : fillerElements.values()) {
            Slot slot = slots.get(element.getStaticIndex());
            if (slot.getElement() == null) {
                slot.setElement(element);
            }
        }
    
        int pageSize = invSize - staticElements.size();
        //int totalPages = (int) Math.ceil(nonStaticElements.size() / (pageSize * 1.0));
        
        int elementStart = (currentPage - 1) * pageSize;
        for (Slot slot : this.slots.values()) {
            if (slot.getElement() == null || !slot.getElement().isStatic()) {
                slot.setElement(nonStaticElements.get(elementStart++));
            }
        }
        
        slots.forEach((index, slot) -> {
            if (slot.getElement() != null) {
                inv.setItem(index, slot.getElement().getItemStack());
            }
        });
        return inv;
    }
    
    public void addElement(Element element) {
        this.elements.add(element);
    }
    
    public void removeElement(int index) {
        this.elements.remove(index);
    }
    
    public Slot getSlot(int index) {
        return this.slots.get(index);
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getTotalPages() {
        int invSize = rows * 9;
        Pair<NormalElements, StaticElements> filtered = filterElements();
        StaticElements staticElements = filtered.secondValue();
        NormalElements nonStaticElements = filtered.firstValue();
        
        int pageSize = invSize - staticElements.size();
        return (int) Math.ceil(nonStaticElements.size() / (pageSize * 1.0));
    }
    
    protected Pair<NormalElements, StaticElements> filterElements() {
        NormalElements normalElements = new NormalElements();
        StaticElements staticElements = new StaticElements();
        for (Element value : this.elements.values()) {
            if (value.isStatic()) {
                staticElements.add(value);
            } else {
                normalElements.add(value);
            }
        }
        return new Pair<>(normalElements, staticElements);
    }
    
    protected static class ElementFilter implements Iterable<Element> {
        final IncrementalMap<Element> elements = new IncrementalMap<>();
        public void add(Element element) {
            elements.add(element);
        }
        public IncrementalMap<Element> getElements() {
            return elements;
        }
        
        public Iterator<Element> iterator() {
            return elements.values().iterator();
        }
        
        public int size() {
            return elements.size();
        }
        
        public Element get(int index) {
            return elements.get(index);
        }
    }
    
    protected static class StaticElements extends ElementFilter {
        
    }
    
    protected static class NormalElements extends ElementFilter {
        
    }
}
