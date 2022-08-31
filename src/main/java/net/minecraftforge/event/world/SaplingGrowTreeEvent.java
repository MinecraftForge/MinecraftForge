/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * SaplingGrowTreeEvent is fired when a sapling grows into a tree.<br>
 * This event is fired during sapling growth in
 * {@link SaplingBlock#advanceTree(ServerLevel, BlockPos, BlockState, Random)} .<br>
 * <br>
 * {@link #pos} contains the coordinates of the growing sapling. <br>
 * {@link #rand} contains an instance of Random for use. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event has a result. {@link HasResult} <br>
 * This result determines if the sapling is allowed to grow. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@HasResult
public class SaplingGrowTreeEvent extends WorldEvent
{
    private final BlockPos pos;
    private final Random rand;
    private Holder<? extends ConfiguredFeature<?, ?>> feature;

    @Deprecated(forRemoval = true, since = "1.18.2")
    public SaplingGrowTreeEvent(LevelAccessor world, Random rand, BlockPos pos)
    {
        this(world, rand, pos, null);
    }

    public SaplingGrowTreeEvent(LevelAccessor world, Random rand, BlockPos pos, Holder<? extends ConfiguredFeature<?, ?>> feature)
    {
        super(world);
        this.rand = rand;
        this.pos = pos;
        this.feature = feature;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public Random getRand()
    {
        return rand;
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
