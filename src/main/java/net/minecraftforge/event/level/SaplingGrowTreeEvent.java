/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * This event is fired whenever a sapling, fungus, mushroom or azalea grows into a tree.
 * This event is fired during sapling growth in
 * {@link SaplingBlock#advanceTree(ServerLevel, BlockPos, BlockState, RandomSource)}.
 * <p>
 * This event is not {@linkplain Cancelable cancellable} but does {@linkplain HasResult have a result}.
 * {@linkplain Result#ALLOW ALLOW} and {@linkplain Result#DEFAULT DEFAULT} will allow the sapling to grow
 * using the features set on the event.
 * {@linkplain Result#DENY DENY} will prevent the sapling from growing.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}.
 * TODO 1.20 rename event
 */
@HasResult
public class SaplingGrowTreeEvent extends LevelEvent
{
    private final RandomSource randomSource;
    private final BlockPos pos;
    private Holder<? extends ConfiguredFeature<?, ?>> feature;

    @Deprecated(forRemoval = true, since = "1.19.2")
    public SaplingGrowTreeEvent(LevelAccessor level, RandomSource randomSource, BlockPos pos)
    {
        this(level, randomSource, pos, null);
    }

    public SaplingGrowTreeEvent(LevelAccessor level, RandomSource randomSource, BlockPos pos, Holder<? extends ConfiguredFeature<?, ?>> feature)
    {
        super(level);
        this.randomSource = randomSource;
        this.pos = pos;
        this.feature = feature;
    }

    /**
     * {@return the random source which initiated the sapling growth}
     */
    public RandomSource getRandomSource()
    {
        return this.randomSource;
    }

    /**
     * {@return the coordinates of the sapling attempting to grow}
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * {@return the holder of the feature which will be placed}
     */
    public Holder<? extends ConfiguredFeature<?, ?>> getFeature() {
        return feature;
    }

    public void setFeature(Holder<? extends ConfiguredFeature<?, ?>> feature) {
        this.feature = feature;
    }
}
