/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraftsingularity.common.util.HasResult;
import net.minecraftsingularity.common.util.Result;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is called when a player attempts to use Bonemeal on a block.
 * It can be canceled to completely prevent any further processing.
 *
 * You can also set the result to ALLOW to mark the event as processed
 * and use up a bonemeal from the stack but do no further processing.
 *
 * setResult(ALLOW) is the same as the old setHandled()
 */
// TODO: Redesign BonemealEvent the whole thing, it doens't make sense.
public final class BonemealEvent extends MutableEvent implements Cancellable, PlayerEvent, HasResult {
    public static final CancellableEventBus<BonemealEvent> BUS = CancellableEventBus.create(BonemealEvent.class);

    private final Player player;
    private final Level level;
    private final BlockPos pos;
    private final BlockState block;
    private final ItemStack stack;
    private Result result = Result.DEFAULT;

    public BonemealEvent(@Nullable Player player, Level level, BlockPos pos, BlockState block, ItemStack stack) {
        this.player = player;
        this.level = level;
        this.pos = pos;
        this.block = block;
        this.stack = stack;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getBlock() {
        return block;
    }

    @NotNull
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public void setResult(Result result) {
        this.result = result;
    }
}
