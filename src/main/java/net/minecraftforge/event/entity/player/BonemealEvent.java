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
import org.jetbrains.annotations.NotNull;

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

    private final Level level;
    private final BlockPos pos;
    private final BlockState block;
    private final ItemStack stack;

    public BonemealEvent(@NotNull Player player, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState block, @NotNull ItemStack stack)
    {
        super(player);
        this.level = level;
        this.pos = pos;
        this.block = block;
        this.stack = stack;
    }

    public Level getLevel()
    {
        return level;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public BlockState getBlock()
    {
        return block;
    }

    @NotNull
    public ItemStack getStack()
    {
        return stack;
    }
}
