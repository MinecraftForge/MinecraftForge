/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.IInventory;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public interface ICraftingHandler
{
    /**
     * Called after an item is taken from crafting.
     */
    public void onTakenFromCrafting(EntityPlayer player, ItemStack stack, IInventory craftMatrix);
}
