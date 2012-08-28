package net.minecraftforge.event.entity.player;

import net.minecraft.src.*;

public class ItemStackEvent extends PlayerEvent 
{
	/**
	 * The item that was either right clicked or eaten.
	 */
	public Item item;
	
	/**
	 * The player that did the action.
	 */
	public EntityPlayer player;
	
	public ItemStackEvent(EntityPlayer player) 
	{
		super(player);
	}
	
	/**
	 * Called when an item is eaten. Such as an potion.
	 */
	public static class FoodEaten extends ItemStackEvent
	{
		public FoodEaten(EntityPlayer player, Item item) 
		{
			super(player);
			this.item = item;
			this.player = player;
		}
	}
	
	/**
	 * Called when an item is right clicked. Such as a map.
	 */
	public static class RightClick extends ItemStackEvent
	{
		public RightClick(EntityPlayer player, Item item) 
		{
			super(player);
			this.item = item;
			this.player = player;
		}
	}
}
