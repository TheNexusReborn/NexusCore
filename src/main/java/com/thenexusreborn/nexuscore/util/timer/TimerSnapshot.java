package com.thenexusreborn.nexuscore.util.timer;

public class TimerSnapshot {
    private final long time;
    private final Timer timer;

    public TimerSnapshot(Timer timer, long time) {
        this.timer = timer;
        this.time = time;
    }

    public Timer getTimer() {
        return timer;
    }

    public void reset() {
        setLength(timer.getLength());
    }

    public void setLength(long l) {
        timer.setLength(l);
    }

    public void setPaused(boolean paused) {
        timer.setPaused(paused);
    }

    public int getSecondsElapsed() {
        return Timer.toSeconds(getTimeElapsed());
    }

    public long getTimeElapsed() {
        return timer.getLength() - time;
    }

    public boolean hasElapsed() {
        return hasElapsed(timer.getLength());
    }

    public boolean hasElapsed(long length) {
        return getTimeElapsed() >= length;
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    public int getSecondsLeft() {
        return Timer.toSeconds(getTimeLeft());
    }

    public long getTimeLeft() {
        return time;
    }

    public void run() {
        timer.run();
    }

    public void run(long length) {
        timer.run(length);
    }
}
