package com.thenexusreborn.nexuscore.util.region;

import org.bukkit.*;

/**
 * This represents a player selection
 */
public class Selection {

    private World world;
    private Location pointA, pointB;

    public Selection(Location pointA, Location pointB) throws IllegalStateException {
        if (!pointA.getWorld().getName().equalsIgnoreCase(pointB.getWorld().getName())) {
            throw new IllegalStateException("Corners are not in the same world!");
        }

        this.world = pointA.getWorld();
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public Selection() {}

    public Location getPointA() {
        return pointA;
    }

    public void setPointA(Location pointA) throws IllegalStateException {
        if (pointB != null) {
            if (!pointB.getWorld().getName().equalsIgnoreCase(pointA.getWorld().getName())) {
                throw new IllegalStateException("Corners are not in the same world!");
            }
        }

        this.pointA = pointA;
        if (world == null) this.world = pointA.getWorld();
    }

    public Location getPointB() {
        return pointB;
    }

    public void setPointB(Location pointB) throws IllegalStateException {
        if (pointA != null) {
            if (!pointA.getWorld().getName().equalsIgnoreCase(pointB.getWorld().getName())) {
                throw new IllegalStateException("Corners are not in the same world!");
            }
        }

        this.pointB = pointB;
        if (world == null) this.world = pointB.getWorld();
    }

    public World getWorld() {
        return world;
    }

    public boolean hasMinimum() {
        return pointA != null;
    }

    public boolean hasMaximum() {
        return pointB != null;
    }
}