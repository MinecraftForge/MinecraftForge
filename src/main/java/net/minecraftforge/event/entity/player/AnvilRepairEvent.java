package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AnvilRepairEvent extends PlayerEvent
{
    /**
     * Fired when the player removes a "repaired" item from the Anvil's Output slot.
     *
     * breakChance specifies as a percentage the chance that the anvil will be "damaged" when used.
     *
     * ItemStacks are the inputs/output from the anvil. They cannot be edited.
     */
    
    public final ItemStack left; // The left side of the input
    public final ItemStack right; // The right side of the input
    public final ItemStack output; // Set this to set the output stack
    public float breakChance; // Anvil's chance to break (reduced by 1 durability) when this is complete. Default is 12% (0.12f)

    public AnvilRepairEvent(EntityPlayer player, ItemStack output, ItemStack left, ItemStack right)
    {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.breakChance = 0.12f;
    }
}
