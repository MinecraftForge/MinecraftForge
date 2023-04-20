/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public interface IForgeDispensibleContainerItem
{
    private DispensibleContainerItem self()
    {
        return (DispensibleContainerItem)this;
    }

    /**
     * Empties the contents of the container and returns whether it was successful.
     *
     * @param player    Player who empties the container. May be null for blocks like dispensers.
     * @param level     Level to place the content in
     * @param pos       The position in the level to empty the content
     * @param hitResult Hit result of the interaction. May be null for blocks like dispensers.
     * @param container ItemStack of the container. May be null for backwards compatibility.
     * @return true if emptying the contents of the container was successful, false otherwise
     */
    default boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult hitResult, @Nullable ItemStack container)
    {
        return self().emptyContents(player, level, pos, hitResult);
    }
}
