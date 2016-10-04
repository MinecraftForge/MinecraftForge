package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerArmorChangeEvent extends PlayerEvent
{

	public final int armorSlot;
	public final ItemStack oldStack, newStack;

	/**
	 * This event is fired when a player's armor inventory is changed.
	 */
	public PlayerArmorChangeEvent(EntityPlayer player, int slot, ItemStack oldItemStack, ItemStack newItemStack)
	{
		super(player);
		this.armorSlot = slot;
		this.oldStack = oldItemStack;
		this.newStack = newItemStack;
	}
}
