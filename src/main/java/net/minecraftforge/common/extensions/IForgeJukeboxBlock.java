/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Extension added to deal with mojang hardcoding the logic for playing records and parrot/allay dancing to only the vanilla jukebox.
 *
 * @see JukeboxBlock
 */
public interface IForgeJukeboxBlock
{

    /**
     * @param entity The entity inserting into jukebox. This can be null.
     * @param level The level of the jukebox.
     * @param pos The position of the jukebox.
     * @param state The blockstate of the jukebox
     * @param stack The itemstack being inserted.
     */
    default void insertRecord(@Nullable Entity entity, LevelAccessor level, BlockPos pos, BlockState state, ItemStack stack)
    {
        if (this instanceof JukeboxBlock jukebox)
        {
            jukebox.setRecord(entity, level, pos, state, stack);
        }
    }

    /**
     * Get the record of the jukebox.
     *
     * @param level The level of the jukebox.
     * @param pos The position of the jukebox.
     * @return Returns the current record or empty;
     */
    default ItemStack getRecord(LevelAccessor level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox) return jukebox.getRecord();
        return ItemStack.EMPTY;
    }

    /**
     * Check if entity is able to dance while near this jukebox
     *
     * @param entity The entity being check
     * @return Return true if the entity can dance
     */
    default boolean canDance(Entity entity)
    {
        return entity instanceof Allay || entity instanceof Parrot;
    }

    /**
     * @param level The level of the jukebox.
     * @param pos The position of the jukebox.
     * @param state The blockstate of the jukebox
     * @return Return true if jukebox has a record.
     */
    default boolean hasRecord(Level level, BlockPos pos, BlockState state)
    {
        if (this instanceof JukeboxBlock) return !state.getValue(JukeboxBlock.HAS_RECORD);
        return false;
    }
}
