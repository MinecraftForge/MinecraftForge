/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Return a crafting handler for the mod container to call
 *
 * @author cpw
 *
 */
public interface ICraftingHandler
{
    /**
     * The object array contains these three arguments
     *
     * @param player
     * @param item
     * @param craftMatrix
     */
    void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix);

    /**
     * The object array contains these two arguments
     * @param player
     * @param item
     */
    void onSmelting(EntityPlayer player, ItemStack item);
}
