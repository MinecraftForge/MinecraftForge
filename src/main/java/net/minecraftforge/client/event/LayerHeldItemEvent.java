package net.minecraftforge.client.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Event for changing the held item being rendered for an entity
 */
public class LayerHeldItemEvent extends Event{

	private ItemStack stack;
	private final EntityLivingBase living;
	private final EnumHand hand;
	public LayerHeldItemEvent(EntityLivingBase living, ItemStack stack, EnumHand hand)
	{
		this.living=living;
		this.stack=stack;
		this.hand=hand;
	}
	
	public EnumHand getHand()
	{
		return this.hand;
	}
	
	public EntityLivingBase getEntity()
	{
		return this.living;
	}
	
	public ItemStack getStack()
	{
		return this.stack;
	}
	
	public void setStack(ItemStack stack)
	{
		this.stack = stack;
	}
}
