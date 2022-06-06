package net.minecraftforge.event.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Base class for all events that modifies the behavior of ItemStack.
 * Some Events has LivingEntity as stack holder, some has Player as stack holder.
 * Thus, this event will extend LivingEvent, and those which has Player as holder
 * will extend this event instead of PlayerEvent.
 */
public class ItemStackEvent extends LivingEvent {

	private final ItemStack stack;

	public ItemStackEvent(LivingEntity user, ItemStack stack) {
		super(user);
		this.stack = stack;
	}

	public ItemStack getStack() {
		return stack;
	}

}
