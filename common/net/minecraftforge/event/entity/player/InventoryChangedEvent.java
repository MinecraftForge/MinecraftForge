package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryChangedEvent extends PlayerEvent {
	
    /**
     *This event is called when a slot in the player's inventory is changed.
     *
     * This event has the following properties:
     * @param slot The slot being changed. 
     * @param ItemStack The ItemStack from the slot.
     * @param isArmor Whether or not the slot is an armor slot.
     *       36 = boots
     *       37 = pants
     *       38 = chest
     *       39 = helmet
     **/
	
	public final int slot;
	public final ItemStack itemstack;
	public final Boolean isArmor;
	
	public InventoryChangedEvent(EntityPlayer player, int slot, ItemStack itemstack) {
		super(player);
		this.slot = slot;
		this.itemstack = itemstack;
		this.isArmor = (slot > 35 && slot < 40);
	}

}
