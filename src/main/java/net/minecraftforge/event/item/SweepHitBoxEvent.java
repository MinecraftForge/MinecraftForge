package net.minecraftforge.event.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * Event that modifies the Sweeping Edge Hit Box of items
 */
public class SweepHitBoxEvent extends PlayerItemStackEvent {

	private final Entity target;
	private final AABB originalHitBox;
	private AABB resultHitBox;

	public SweepHitBoxEvent(Player player, ItemStack stack, Entity target, AABB hitBox) {
		super(player, stack);
		this.target = target;
		this.originalHitBox = hitBox;
		this.resultHitBox = hitBox;
	}

	/**
	 * @return the entity being attacked
	 */
	public Entity getTarget() {
		return target;
	}

	/**
	 * @return the original hitbox as given by the item
	 */
	public AABB getOriginalHitBox() {
		return originalHitBox;
	}

	/**
	 * get modified hit box
	 */
	public AABB getResultHitBox() {
		return resultHitBox;
	}

	/**
	 * modify the hit box
	 */
	public void setResultHitBox(AABB hitBox) {
		resultHitBox = hitBox;
	}

	/**
	 * inflate the hit box
	 */
	public void inflate(double x, double y, double z) {
		resultHitBox = resultHitBox.inflate(x, y, z);
	}

}
