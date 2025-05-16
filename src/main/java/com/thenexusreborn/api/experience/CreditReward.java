package com.thenexusreborn.api.experience;

import com.thenexusreborn.api.player.NexusPlayer;
import com.thenexusreborn.api.reward.Reward;

public class CreditReward extends Reward {

    private final int credits;

    public CreditReward(int credits) {
        super("credit_reward_" + credits, credits + " Credits");
        this.credits = credits;
    }

    @Override
    public void applyReward(NexusPlayer profile) {
        profile.addCredits(credits);
    }
}
