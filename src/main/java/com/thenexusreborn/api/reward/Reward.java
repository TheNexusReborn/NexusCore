package com.thenexusreborn.api.reward;

import com.thenexusreborn.api.player.NexusPlayer;

public abstract class Reward {
    protected final String id, name;

    public Reward(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract void applyReward(NexusPlayer profile);
}
