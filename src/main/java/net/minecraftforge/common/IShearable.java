/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

/**
 *
 * This allows for mods to create there own Shear-like items
 * and have them interact with Blocks/Entities without extra work.
 * Also, if your block/entity supports the Shears, this allows you
 * to support mod-shears as well.
 *
 */
//TODO Change to World, not IBlockAccess and make Implementor responsible for removing itself from the world.
//Better mimics vanilla behavior and allows more control for the user.
public interface IShearable
{
    /**
     * Checks if the object is currently shearable
     * Example: Sheep return false when they have no wool
     *
     * @param item The ItemStack that is being used, may be empty.
     * @param world The current world.
     * @param pos Block's position in world.
     * @return If this is shearable, and onSheared should be called.
     */
    boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos);

    /**
     * Performs the shear function on this object.
     * This is called for both client, and server.
     * The object should perform all actions related to being sheared,
     * except for dropping of the items, and removal of the block.
     * As those are handled by ItemShears itself.
     *
     * Returns a list of items that resulted from the shearing process.
     *
     * For entities, they should trust there internal location information
     * over the values passed into this function.
     *
     * @param item The ItemStack that is being used, may be empty.
     * @param world The current world.
     * @param pos If this is a block, the block's position in world.
     * @param fortune The fortune level of the shears being used.
     * @return A List containing all items from this shearing. May be empty.
     */
    @Nonnull
    List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune);
}
