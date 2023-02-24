package com.thenexusreborn.nexuscore.clock;

import com.thenexusreborn.nexuscore.clock.callback.*;
import com.thenexusreborn.nexuscore.clock.impl.Stopwatch;
import com.thenexusreborn.nexuscore.clock.snapshot.ClockSnapshot;
import com.thenexusreborn.nexuscore.util.timer.Timer;

import java.util.*;

/**
 * A utility class to allow having scheduled tasks run on a clock. This class is a default parent class<br>
 * Clocks do not do much outside of keeping track of things. ClockCallbacks are used for determining what to do with a clock.
 *
 * @param <T> The ClockSnapshot type
 * @see Timer for countdown clocks
 * @see Stopwatch for countup clocks
 */
public abstract class Clock<T extends ClockSnapshot> {
    /**
     * A list of all clocks. This is needed for the clocks to run in the ClockThread
     */
    private static final List<Clock<? extends ClockSnapshot>> CLOCKS = new ArrayList<>();
    
    /**
     * The current time of this clock. See the count() method for how this is manipulated
     */
    protected long time;
    
    /**
     * Control variable for if this clock is paused. Please see the pause() and unpause() methods.
     */
    protected boolean paused = true;
    
    /**
     * Control variable for if this clock is cancelled. Please see the cancel() and uncancel() methods.
     */
    protected boolean cancelled;
    
    /**
     * A map of all the callbacks registered to this Clock instance. The CallbackHolder class is just a class to manage information on the callback.<br>
     * The UUID is internal for this clock and allows easy identification of a callback due to the fact that the ClockCallback is an interface.
     */
    protected Map<UUID, CallbackHolder<T>> callbacks = new HashMap<>();
    
    /**
     * Basic constructor for a clock. This adds the clock to a global static list. This is how the thread is able to find existing clocks
     */
    public Clock() {
        CLOCKS.add(this);
    }
    
    /**
     * Constructs a clock with a starting time argument
     *
     * @param time The time to start at in milliseconds
     */
    public Clock(long time) {
        this();
        this.time = time;
    }
    
    /**
     * Constructs a clock with a time and an initial callback. This is useful if you plan on only having one callback for this clock
     *
     * @param time     The time to start at in milliseconds
     * @param callback The callback
     * @param interval The interval in milliseconds to run the callback
     */
    public Clock(long time, ClockCallback<T> callback, long interval) {
        this(time);
        addCallback(callback, interval);
    }
    
    /**
     * This counts the clock. Depending on the implementation, it should count up or down. <br>
     * Due to the fact that clocks operate on a millisecond scale, the count method should take this into account.
     */
    public abstract void count();
    
    /**
     * Creates a ClockSnapshot of this clock, which is a thread-safe way to read information.
     *
     * @return The snapshot instance
     * @see ClockSnapshot
     */
    public abstract T createSnapshot();
    
    /**
     * A method to allow all logic for callbacks to exist in this class so that the callbacks themselves don't need repeat code<br>
     * This method should only really be handled for the interval and last run based times. Other logic is handled by the callback() method
     * @param holder The holder for the callback
     * @return true to continue the clock, false to cancel the clock. Please see the callback() method to know what to return here.
     */
    protected abstract boolean shouldCallback(CallbackHolder<T> holder);
    
    /**
     * This method handles everything related to the callback logic of the clock class.<br>
     * The final return of this method will determine if this clock object is still seen by the thread that runs all clocks<br>
     * This method returns true if the callbacks are empty, if a call to the shouldCallback() method returns false, or if any other callbacks return true<br>
     * This method returns false if all callbacks return false.
     * @return True to continue the clock and keep it registered, false to stop the clock and deregister the clock.
     */
    public boolean callback() {
        if (this.callbacks.isEmpty()) {
            return true;
        }
        
        boolean result = false;
        
        T snapshot = createSnapshot();
        for (CallbackHolder<T> holder : this.callbacks.values()) {
            ClockCallback<T> callback = holder.getCallback();
            if (callback == null) {
                continue;
            }
            
            if (!holder.getStatus()) {
                continue;
            }
            
            if (holder.getInterval() > 0) {
                continue;
            }
            
            if (!shouldCallback(holder)) {
                result = true;
                continue;
            }
            
            holder.setLastRun(time);
            boolean callbackResult = callback.callback(snapshot);
            holder.setStatus(callbackResult);
            if (!result) {
                result = callbackResult;
            }
        }
        
        return result;
    }
    
    /**
     * Starts this clock
     * @return The current clock instance
     */
    public Clock<T> start() {
        unpause();
        return this;
    }
    
    /**
     * Pauses this clock from running. <br>
     * Note: This control variable just makes the thread ignore this clock until the unpause method is called. This keeps existing information
     */
    public void pause() {
        this.paused = true;
    }
    
    /**
     * Resumes this clock where it left off.
     */
    public void unpause() {
        this.paused = false;
    }
    
    /**
     * Cancels this clock altogether. This will prevent it from running anything in the future.
     */
    public void cancel() {
        CLOCKS.remove(this);
        this.cancelled = true;
    }
    
    /**
     * Reregisters this clock to allow things to keep running from where it left off. You must maintain a reference to use the same clock.
     */
    public void uncancel() {
        CLOCKS.add(this);
        this.cancelled = false;
    }
    
    /**
     * @return If this clock has been cancelled. This can be through the cancel() methods or through the automated detection
     */
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * @return The current time value
     */
    public long getTime() {
        return time;
    }
    
    /**
     * @return If this clock is paused
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Adds time to the time value
     * @param time The time in millseconds to add
     */
    public void addTime(long time) {
        this.time += time;
    }
    
    /**
     * Removes time from the time value
     * @param time The time in millseconds to remove
     */
    public void removeTime(long time) {
        this.time -= time;
    }
    
    /**
     * Sets the time value to the supplied argument's value
     * @param time The time to set it to
     */
    public void setTime(long time) {
        this.time = time;
    }
    
    /**
     * Adds a callback to this Clock
     * @param callback The callback to add
     * @param interval The interval in millseconds
     * @return The generated UUID of the callback. You can use this to get this exact callback again if you need
     */
    public UUID addCallback(ClockCallback<T> callback, long interval) {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (this.callbacks.containsKey(uuid));
        
        this.callbacks.put(uuid, new CallbackHolder<>(callback, uuid, interval));
        return uuid;
    }
    
    /**
     * Gets the instance of a callback
     * @param uuid The UUID of the callback
     * @return The callback instance
     */
    public ClockCallback<T> getCallback(UUID uuid) {
        CallbackHolder<T> holder = this.callbacks.get(uuid);
        if (holder != null) {
            return holder.getCallback();
        }
        
        return null;
    }
    
    /**
     * @return All registered clocks. This returns a copy of the original list, this is thread safe for read-only operations.
     */
    public static List<Clock<? extends ClockSnapshot>> getClocks() {
        return new ArrayList<>(CLOCKS);
    }
}