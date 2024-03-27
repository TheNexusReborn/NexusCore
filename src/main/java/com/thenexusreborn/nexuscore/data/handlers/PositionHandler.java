package com.thenexusreborn.nexuscore.data.handlers;

import com.stardevllc.starmclib.Position;
import com.thenexusreborn.api.sql.objects.TypeHandler;

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

            int x = (int) Double.parseDouble(split[0]);
            int y = (int) Double.parseDouble(split[1]);
            int z = (int) Double.parseDouble(split[2]);
            float yaw = (int) Double.parseDouble(split[3]);
            float pitch = (int) Double.parseDouble(split[4]);
            return new Position(x, y, z, yaw, pitch);
        });
    }
}
