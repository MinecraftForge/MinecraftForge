package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerRepairEvent extends PlayerEvent
{
    public final ItemStack left;      // The left side of the input
    public final ItemStack right;     // The right side of the input
    public final ItemStack output;    // Set this to set the output stack
    
    public PlayerRepairEvent(EntityPlayer player, ItemStack output, ItemStack left, ItemStack right)
    {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
    }
}
