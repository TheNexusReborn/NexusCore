package com.thenexusreborn.nexuscore.scoreboard.impl;

import com.thenexusreborn.api.scoreboard.wrapper.IScore;
import org.bukkit.scoreboard.Score;

public class SpigotScore implements IScore {
    
    private final Score score;
    
    public SpigotScore(Score score) {
        this.score = score;
    }
    
    @Override
    public void setScore(int score) {
        this.score.setScore(score);
    }
}
