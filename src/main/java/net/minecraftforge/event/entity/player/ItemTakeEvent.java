package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import javax.annotation.Nonnull;

@Cancelable
public class ItemTakeEvent extends PlayerEvent
{

	/**
	 * This event is fired in {@link net.minecraft.inventory.Slot#canTakeStack(EntityPlayer)} when a player clicks on an item.
	 */
	@Nonnull
	private final ItemStack itemStack;
	private EnumActionResult cancellationResult = EnumActionResult.PASS;

	public ItemTakeEvent(@Nonnull ItemStack itemStack, EntityPlayer entityPlayer) {
		super(entityPlayer);
		this.itemStack = itemStack;
	}

	/**
	 * The {@link ItemStack} being moved.
	 */
	@Nonnull
	public ItemStack getItemStack()
	{
		return itemStack;
	}

	/**
	 * @return The EnumActionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
	 * method of the event. By default, this is {@link EnumActionResult#PASS}, meaning cancelled events will cause
	 * the client to keep trying more interactions until something works.
	 */
	public EnumActionResult getCancellationResult()
	{
		return cancellationResult;
	}

	/**
	 * Set the EnumActionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
	 * method of the event.
	 */
	public void setCancellationResult(EnumActionResult result)
	{
		this.cancellationResult = result;
	}
}
