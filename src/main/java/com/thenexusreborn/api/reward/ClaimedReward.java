package com.thenexusreborn.api.reward;

import com.thenexusreborn.api.sql.annotations.table.TableName;

import java.util.Objects;
import java.util.UUID;

@TableName("claimedrewards")
public class ClaimedReward {
    private long id;
    private UUID uuid;
    private String rewardId;
    private long timestamp;

    public ClaimedReward(UUID uuid, String rewardId, long timestamp) {
        this.uuid = uuid;
        this.rewardId = rewardId;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getRewardId() {
        return rewardId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClaimedReward that = (ClaimedReward) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(rewardId, that.rewardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, rewardId);
    }
}
