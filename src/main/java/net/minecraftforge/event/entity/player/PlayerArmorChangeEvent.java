package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.common.MinecraftForge;

/**
 * PlayerArmorChangeEvent is fired when a player's armor inventory is changed.<br>
 * This event is fired whenever a player's armor inventory is changed in
 * InventoryPlayer#setInventorySlotContents(int, ItemStack).<br>
 * <br>
 * {@link #armorSlot} The armor inventory slot which is being changed. <br>
 * <br>
 * {@link #oldStack} The old armor stack (can be null). <br>
 * <br>
 * {@link #newStack} The new armor stack (can be null). <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerArmorChangeEvent extends PlayerEvent
{

	public final int armorSlot;
	public final ItemStack oldStack, newStack;

	public PlayerArmorChangeEvent(EntityPlayer player, int slot, ItemStack oldItemStack, ItemStack newItemStack)
	{
		super(player);
		this.armorSlot = slot;
		this.oldStack = oldItemStack;
		this.newStack = newItemStack;
	}
}
