package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Fired when the player removes a "repaired" item from the Anvil's Output slot.<br>
 * <br>
 * {@link #breakChance} specifies as a percentage the chance that the anvil will be "damaged" when used.<br>
 * <br>
 * ItemStacks are the inputs/output from the anvil. They cannot be edited.
 */
public class AnvilRepairEvent extends PlayerEvent
{

    private final ItemStack left; // The left side of the input
    private final ItemStack right; // The right side of the input
    private final ItemStack output; // Set this to set the output stack
    private float breakChance; // Anvil's chance to break (reduced by 1 durability) when this is complete. Default is 12% (0.12f)

    public AnvilRepairEvent(EntityPlayer player, ItemStack output, ItemStack left, ItemStack right)
    {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.setBreakChance(0.12f);
    }

    public ItemStack getLeft() { return left; }
    public ItemStack getRight() { return right; }
    public ItemStack getOutput() { return output; }
    public float getBreakChance() { return breakChance; }
    public void setBreakChance(float breakChance) { this.breakChance = breakChance; }
}
