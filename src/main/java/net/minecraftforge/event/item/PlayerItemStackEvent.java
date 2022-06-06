package net.minecraftforge.event.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for all events that modifies the behavior of ItemStack, and require Player as user.
 */
public class PlayerItemStackEvent extends ItemStackEvent {

	private final Player user;

	public PlayerItemStackEvent(Player user, ItemStack stack) {
		super(user, stack);
		this.user = user;
	}

	public Player getPlayer() {
		return user;
	}

}
