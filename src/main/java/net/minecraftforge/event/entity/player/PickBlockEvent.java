package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PickBlockEvent extends PlayerEvent
{
	public ItemStack stack;
	public int slot;
	
	public PickBlockEvent(EntityPlayer player, ItemStack stack, int slot)
	{
		super(player);
	}
	
}
