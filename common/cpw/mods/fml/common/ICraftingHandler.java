/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
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
