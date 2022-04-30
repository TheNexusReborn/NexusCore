package com.thenexusreborn.nexuscore.menu.element.button;

import com.thenexusreborn.nexuscore.menu.element.Element;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An Element that is for what happens when it is clicked
 * These can have an action for left and right clicking as well as a sound when it is clicked
 */
public class Button extends Element {
    
    protected ButtonAction leftClickAction, rightClickAction; //The actions used for right and left clicking the item, shift clicking should be handled by the actions
    protected Sound clickSound; //Sound for clicking on the button
    
    /**
     * Constructs a new Button with an Item stack
     * @param itemStack The item for the button
     */
    public Button(ItemStack itemStack) {
        super(itemStack);
    }
    
    /**
     * Constructs a new button with an ItenStack and a Sound
     * @param itemStack The itemstack
     * @param sound The click sound
     */
    public Button(ItemStack itemStack, Sound sound) {
        super(itemStack);
        this.clickSound = sound;
    }
    
    /**
     * Constructs a button with an itemstack and an index in the menu
     * Note: This is used internally
     * @param itemStack The ItemStack
     * @param staticIndex The static index
     */
    public Button(ItemStack itemStack, int staticIndex) {
        super(itemStack, staticIndex);
    }
    
    /**
     * Utility method to play the sound when it is clicked, used internally
     * @param player The player
     */
    public void playSound(Player player) {
        if (clickSound != null) {
            player.playSound(player.getLocation(), clickSound, 1F, 1F);
        }
    }
    
    /**
     * Sets the action for the left click 
     * @param leftClickAction The action
     * @return This Button as a Builder style pattern
     */
    public Button setLeftClickAction(ButtonAction leftClickAction) {
        this.leftClickAction = leftClickAction;
        return this;
    }
    
    /**
     * Sets the action for the right click 
     * @param rightClickAction The action
     * @return This Button as a Builder style pattern
     */
    public Button setRightClickAction(ButtonAction rightClickAction) {
        this.rightClickAction = rightClickAction;
        return this;
    }
    
    /**
     * Gets the current left click action
     * @return The action
     */
    public ButtonAction getLeftClickAction() {
        return leftClickAction;
    }
    
    /**
     * Gets the current right click action
     * @return The action
     */
    public ButtonAction getRightClickAction() {
        return rightClickAction;
    }
    
    /**
     * Sets the click sound
     * @param clickSound The new sound
     */
    public void setClickSound(Sound clickSound) {
        this.clickSound = clickSound;
    }
    
    /**
     * Gets the current click sound
     * @return The sound
     */
    public Sound getClickSound() {
        return clickSound;
    }
}
