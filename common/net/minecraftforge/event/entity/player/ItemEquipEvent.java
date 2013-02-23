package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;

public class ItemEquipEvent extends PlayerEvent {
	
	private final int slot;
	private final InventoryPlayer inventory;
	private ItemStack stack;
	
	public ItemEquipEvent(InventoryPlayer inventory, int slot, ItemStack stack) {
		super(inventory.player);
		this.slot = slot;
		this.inventory = inventory;
		this.stack = stack;
	}
	
	/**
	 * Used to get the ItemEquipEvent Player
	 * @return EntityPlayer
	 */
	public EntityPlayer getPlayer() {
		return inventory.player;
	}
	
	public InventoryPlayer getInventory() {
		return inventory;
	}
	
	/**
	 * Used to get the Armorslot ID
	 * @return int
	 */
	public int getSlot() {
		return slot;
	}
	
	/**
	 * Used to get the ItemEquipEvent ItemStack
	 * @return ItemStack
	 */
	public ItemStack getItemStack() {
		return stack;
	}
}
