package com.thenexusreborn.nexuscore.util;

import org.bukkit.*;

import java.util.Objects;

public class Position {
    protected int x, y, z;
    protected float yaw, pitch;
    
    public static Position fromLocation(Location location) {
        return new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
    }

    public Position() {
        this(0, 0, 0);
    }

    public Position(int x, int y, int z) {
        this(x, y, z, 0, 0);
    }

    public Position(int x, int y, int z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + "," + yaw + "," + pitch + ')';
    }
    
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Position position = (Position) o;
        return x == position.x && y == position.y && z == position.z;
    }

    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
