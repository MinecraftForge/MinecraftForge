/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;

public interface IFuelHandler
{
    /** Called when a furnace gains fuel to get its burn time.
     * @return fuel burn time in ticks or 0 to continue processing
     */
    public int getItemBurnTime(ItemStack stack);
}