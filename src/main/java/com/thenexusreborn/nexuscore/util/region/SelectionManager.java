package com.thenexusreborn.nexuscore.util.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * This manages selections. 
 * This class is provided as a Bukkit Service from StarMCUtils. Use that to get a copy of the instance of this class, or create one yourself
 */
public class SelectionManager {

    private final Map<UUID, Selection> selections = new HashMap<>();

    public void setPointA(UUID player, Location pointA) {
        if (this.selections.containsKey(player)) {
            this.selections.get(player).setPointA(pointA);
        } else {
            Selection selection = new Selection();
            selection.setPointA(pointA);
            this.selections.put(player, selection);
        }
    }

    public void setPointB(UUID player, Location pointB) {
        if (this.selections.containsKey(player)) {
            this.selections.get(player).setPointB(pointB);
        } else {
            Selection selection = new Selection();
            selection.setPointB(pointB);
            this.selections.put(player, selection);
        }
    }

    public Cuboid getCuboid(UUID player) {
        if (this.selections.containsKey(player)) {
            Location pointA = this.selections.get(player).getPointA(), pointB = this.selections.get(player).getPointB();
            if (pointA == null || pointB == null) {
                return null;
            }

            return new Cuboid(pointA, pointB);
        }

        return null;
    }

    public boolean hasSelection(UUID uuid) {
        return this.selections.containsKey(uuid);
    }

    public void clearSelection(UUID player) {
        this.selections.remove(player);
    }

    public Selection getSelection(UUID player) {
        if (!this.selections.containsKey(player)) {
            this.selections.put(player, new Selection());
        }

        return this.selections.get(player);
    }

    public void setPointA(Player player, Location pointA) {
        this.setPointA(player.getUniqueId(), pointA);
    }

    public void setPointB(Player player, Location pointB) {
        this.setPointB(player.getUniqueId(), pointB);
    }

    public boolean hasSelection(Player player) {
        return this.hasSelection(player.getUniqueId());
    }

    public Cuboid getCuboid(Player player) {
        return this.getCuboid(player.getUniqueId());
    }

    public Selection getSelection(Player player) {
        return this.getSelection(player.getUniqueId());
    }

    public void clearSelection(Player player) {
        this.clearSelection(player.getUniqueId());
    }
}
