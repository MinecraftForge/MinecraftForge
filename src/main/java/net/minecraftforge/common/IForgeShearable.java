/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * This allows for mods to create there own Shear-like items
 * and have them interact with Blocks/Entities without extra work.
 * Also, if your block/entity supports the Shears, this allows you
 * to support mod-shears as well.
 *
 */
public interface IForgeShearable
{
    /**
     * Checks if the object is currently shearable
     * Example: Sheep return false when they have no wool
     *
     * @param item The ItemStack that is being used, may be empty.
     * @param level The current level.
     * @param pos Block's position in level.
     * @return If this is shearable, and onSheared should be called.
     */
    default boolean isShearable(@Nonnull ItemStack item, Level level, BlockPos pos)
    {
        return true;
    }

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
     * @param level The current level.
     * @param pos If this is a block, the block's position in level.
     * @param fortune The fortune level of the shears being used.
     * @return A List containing all items from this shearing. May be empty.
     */
    @Nonnull
    default List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level level, BlockPos pos, int fortune)
    {
        return Collections.emptyList();
    }
}
