/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * This event is called when a player attempts to use Bonemeal on a block.
 * It can be canceled to completely prevent any further processing.
 *
 * You can also set the result to ALLOW to mark the event as processed
 * and use up a bonemeal from the stack but do no further processing.
 *
 * setResult(ALLOW) is the same as the old setHandled()
 */
@Cancelable
@net.minecraftforge.eventbus.api.Event.HasResult
public class BonemealEvent extends PlayerEvent
{

    private final World world;
    private final BlockPos pos;
    private final BlockState block;
    private final ItemStack stack;

    public BonemealEvent(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull ItemStack stack)
    {
        super(player);
        this.world = world;
        this.pos = pos;
        this.block = block;
        this.stack = stack;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public BlockState getBlock()
    {
        return block;
    }

    @Nonnull
    public ItemStack getStack()
    {
        return stack;
    }
}
