package com.thenexusreborn.nexuscore.data.handlers;

import me.firestar311.starlib.spigot.utils.Position;
import me.firestar311.starsql.api.objects.TypeHandler;

public class PositionHandler extends TypeHandler {
    public PositionHandler() {
        super(Position.class, "varchar(100)", (column, object) -> {
            Position position = (Position) object;
            if (position == null) {
                return null;
            }
            return position.getX() + "," + position.getY() + "," + position.getZ() + "," + position.getYaw() + "," + position.getPitch();
        }, (column, object) -> {
            if (object == null) {
                return null;
            }
            String[] split = object.toString().split(",");
            if (split.length != 5) {
                return null;
            }

            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            float yaw = Float.parseFloat(split[3]);
            float pitch = Float.parseFloat(split[4]);
            return new Position(x, y, z, yaw, pitch);
        });
    }
}
