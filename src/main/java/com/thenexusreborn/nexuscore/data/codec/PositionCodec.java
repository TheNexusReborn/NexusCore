package com.thenexusreborn.nexuscore.data.codec;

import com.thenexusreborn.nexuscore.util.Position;
import me.firestar311.starsql.api.objects.SqlCodec;

public class PositionCodec implements SqlCodec<Position> {
    @Override
    public String encode(Object object) {
        Position position = (Position) object;
        return position.getX() + "," + position.getY() + "," + position.getZ() + "," + position.getYaw() + "," + position.getPitch();
    }
    
    @Override
    public Position decode(String encoded) {
        String[] split = encoded.split(",");
        if (split == null || split.length != 5) {
            return null;
        }
        
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        float yaw = Float.parseFloat(split[3]);
        float pitch = Float.parseFloat(split[4]);
        return new Position(x, y, z, yaw, pitch);
    }
}
