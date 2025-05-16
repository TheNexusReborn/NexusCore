package com.thenexusreborn.api.reward;

import java.util.*;

public class RewardManager {
    private Map<String, Reward> rewards = new HashMap<>();
    private Set<ClaimedReward> claimedRewards = new HashSet<>();

    public void addReward(Reward reward) {
        this.rewards.put(reward.getId(), reward);
    }

    public Reward getReward(String id) {
        return this.rewards.get(id);
    }

    public Collection<Reward> getRewards() {
        return new ArrayList<>(this.rewards.values());
    }

    public void addClaimedReard(ClaimedReward claimedReward) {
        this.claimedRewards.add(claimedReward);
    }

    public Collection<ClaimedReward> getClaimedRewards() {
        return new ArrayList<>(this.claimedRewards);
    }
}
