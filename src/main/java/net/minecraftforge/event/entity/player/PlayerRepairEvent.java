package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Fired when the player removes a "repaired" item from the Anvil's Output slot.
 * 
 * breakChance provides a multiplier on the base 12% chance for the anvil to be damaged.
 * Set breakChance to 0.0F to prevent the anvil being damaged.
 * 2.0F makes the anvil twice as likely to be damaged (24%).
 * 
 * ItemStacks are the inputs/output from the anvil.  They cannot be edited.
 * 
 */
public class PlayerRepairEvent extends PlayerEvent
{
    public final ItemStack left;      // The left side of the input
    public final ItemStack right;     // The right side of the input
    public final ItemStack output;    // Set this to set the output stack
    public float breakChance;        // Modifies chance to break. Multiplicative
    
    public PlayerRepairEvent(EntityPlayer player, ItemStack output, ItemStack left, ItemStack right)
    {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.breakChance = 1.0f;
    }
}
