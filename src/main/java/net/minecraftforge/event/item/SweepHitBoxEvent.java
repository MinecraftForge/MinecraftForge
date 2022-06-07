/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * Event that modifies the Sweeping Edge Hitbox of items
 */
public class SweepHitBoxEvent extends PlayerItemStackEvent
{

	private final Entity target;
	private final AABB originalHitBox;
	private AABB resultHitBox;

	public SweepHitBoxEvent(Player player, ItemStack stack, Entity target, AABB hitBox)
	{
		super(player, stack);
		this.target = target;
		this.originalHitBox = hitBox;
		this.resultHitBox = hitBox;
	}

	/**
	 * @return the entity being attacked
	 */
	public Entity getTarget()
	{
		return target;
	}

	/**
	 * @return the original hitbox as given by the item
	 */
	public AABB getOriginalHitBox()
	{
		return originalHitBox;
	}

	/**
	 * @return the modified hitbox
	 */
	public AABB getResultHitBox()
	{
		return resultHitBox;
	}

	/**
	 * Set the hitbox.
	 */
	public void setResultHitBox(AABB hitBox)
	{
		resultHitBox = hitBox;
	}

	/**
	 * Inflates the hitbox by the specified amount.
	 * @see AABB#inflate
	 */
	public void inflate(double x, double y, double z)
	{
		resultHitBox = resultHitBox.inflate(x, y, z);
	}

}
