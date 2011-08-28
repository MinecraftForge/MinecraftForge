/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemArmor;

/**
 * This interface is to be implemented by ItemArmor classes. It will allow to
 * modify computation of damage and health loss. Computation will be called
 * before the actual armor computation, which can then be cancelled.
 *
 * @see ItemArmor
 */
public interface ISpecialArmor {
    
    /**
     * Return extra properties for the armor
     */
	public ArmorProperties getProperties(EntityPlayer player,
			int intitialDamage, int currentDamage);
}
