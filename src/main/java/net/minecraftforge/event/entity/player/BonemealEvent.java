/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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

    private final Level world;
    private final BlockPos pos;
    private final BlockState block;
    private final ItemStack stack;

    public BonemealEvent(@Nonnull Player player, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull ItemStack stack)
    {
        super(player);
        this.world = world;
        this.pos = pos;
        this.block = block;
        this.stack = stack;
    }

    public Level getWorld()
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
