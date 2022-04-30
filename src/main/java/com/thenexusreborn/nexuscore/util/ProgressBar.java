package com.thenexusreborn.nexuscore.util;

/**
 * A utility class to represent a progress bar
 */
public class ProgressBar {

    private final int max;
    private final int totalBars;
    private final String symbol;
    private final String completedColor;
    private final String notCompletedColor;
    private int progress;
    
    /**
     * Constructs a progress bar
     * @param progress The current progress
     * @param max The maximum for the progress to reach
     * @param totalBars How many bars represent this progress bar
     * @param symbol The symbol of the bars
     * @param completedColor The color for the completed percentage
     * @param notCompletedColor The color for the not completed percentage
     */
    public ProgressBar(int progress, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {
        this.progress = progress;
        this.max = max;
        this.totalBars = totalBars;
        this.symbol = symbol;
        this.completedColor = completedColor;
        this.notCompletedColor = notCompletedColor;
    }
    
    /**
     * Sets the current progress
     * @param completed The new progress value
     */
    public void setProgress(int completed) {
        this.progress = completed;
    }
    
    /**
     * Gets a string for the current progress
     * @return The formatted string
     */
    public String display() {
        float percent = (float) progress / max;
        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);
        
        StringBuilder completedBuilder = new StringBuilder(), notCompletedBuilder = new StringBuilder();
        for (int i = 0; i < leftOver; i++) {
            notCompletedBuilder.append(symbol);
        }
    
        for (int i = 0; i < progressBars; i++) {
            completedBuilder.append(symbol);
        }
        
        return MCUtils.color(completedColor + completedBuilder + notCompletedColor + notCompletedBuilder);
    }
    
    /**
     * Gets the current percentage of the progress
     * @return The percentage
     */
    public double getPercentage() {
        int percent = progress * 100 / max;
        return Math.round(percent * 10.0);
    }
}
