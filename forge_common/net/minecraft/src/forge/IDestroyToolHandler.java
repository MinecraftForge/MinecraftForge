/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;
import net.minecraft.src.EntityPlayer;

public interface IDestroyToolHandler
{
    /** Called when the user's currently equipped item is destroyed.
     */
    public void onDestroyCurrentItem(EntityPlayer player, ItemStack orig);
}

