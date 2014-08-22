package net.minecraftforge.common;

import net.minecraft.block.BlockDoor;

/**
 * <p>Types of interaction Entity-AI can perform on doors (extending {@link BlockDoor}).</p>
 *
 */
public enum AIDoorInteraction {
    
    /**
     * <p>Toggle the state of the door (open, close).</p>
     */
    TOGGLE,
    /**
     * <p>Break the door.</p>
     */
    BREAK

}
