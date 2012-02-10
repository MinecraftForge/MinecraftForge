/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;

/**
 * This interface is to be implemented by ItemArmor classes. It will allow to
 * modify computation of damage and health loss. Computation will be called
 * before the actual armor computation, which can then be cancelled.
 *
 * @see ItemArmor
 */
public interface ISpecialArmor 
{
   /** 
    * Apply an armor effect to incoming damage.  This should both compute
    * the damage reduction properties of the armor against the incoming
    * damage, and reduce the armor durability if appropriate.  If the
    * armor is destroyed, decrement the stack size of the ItemStack.  It
    * will then be cleaned up automatically.
    *
    * @param player The player wearing the armor.
    * @param damageSource The source of the damage, which can be used to alter armor
    *     properties based on the type or source of damage.
    * @param armor The ItemStack of the armor item itself.  If you should
    *     need the index in damageArmor, use the armorType field in the item.
    * @param damage The damage being applied to the armor.
    *
    */
	public ArmorProperties getProperties(EntityPlayer player, DamageSource damageSource, ItemStack armor, int damage);
	
	/**
    * Get the displayed effective armor.
    * 
    * @param player The player wearing the armor.
    * @param armor The ItemStack of the armor item itself.  If you should
    *      need the index in damageArmor, use the armorType field in the item.
    * @return The number of armor points for display, 2 per shield.
    */
	public abstract int getArmorDisplay(EntityPlayer player, ItemStack armor);
}
