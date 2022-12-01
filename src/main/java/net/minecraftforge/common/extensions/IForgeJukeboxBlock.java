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
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Extension-Interface providing methods for various jukebox activiites that are currently hardcoded to only the vanilla jukebox.
 * Any {@link Block} implementing will be treated as if it was a jukebox.
 *
 * @see JukeboxBlock
 */
public interface IForgeJukeboxBlock
{

    /**
     * Called by {@link RecordItem} to insert a record into the jukebox.
     * The actual action taken by the jukebox is up to it and not the record.
     *
     * @param state The blockstate of the jukebox.
     * @param level The level of the jukebox.
     * @param pos The position of the jukebox.
     * @param entity The entity inserting into jukebox. This can be null.
     * @param stack The itemstack being inserted.
     */
    default void insertRecord(BlockState state, Level level, BlockPos pos, @Nullable Entity entity, ItemStack stack)
    {
        if (this instanceof JukeboxBlock jukebox)
        {
            jukebox.setRecord(entity, level, pos, state, stack);
        }
    }

    /**
     * Gets the {@link ItemStack} of the current record in the jukebox.
     * The current record can be empty or any item. In vanilla, this can only be records.
     *
     * @param state The blockstate of the jukebox.
     * @param level The level of the jukebox.
     * @param pos The position of the jukebox.
     * @return the current record or empty.
     */
    default ItemStack getRecord(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox) {
            return jukebox.getRecord();
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Checks if an entity is allow to dance close to the jukebox.
     * The actual behavior is controlled by the entity.
     * Right now only the vanilla parrot/allay currently use this.
     *
     * @param state The blockstate of the jukebox
     * @param entity The entity being checked.
     * @return true if the entity can dance.
     */
    default boolean canDance(BlockState state, Entity entity)
    {
        if (entity instanceof Allay) {
            return true;
        } else if (entity instanceof Parrot) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets if the jukebox currently has a record inside it.
     *
     * @param state The blockstate of the jukebox.
     * @param level The level of the jukebox.
     * @param pos The position of the jukebox.
     * @return true if jukebox has a record.
     */
    default boolean hasRecord(BlockState state, Level level, BlockPos pos)
    {
        if (this instanceof JukeboxBlock) {
            return !state.getValue(JukeboxBlock.HAS_RECORD);
        } else {
            return false;
        }
    }
}
