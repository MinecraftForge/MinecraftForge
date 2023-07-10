/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * This event is fired when {@link net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator#placeBlockAt(TreeDecorator.Context, BlockPos)}
 * attempts to alter a ground block when generating a feature. An example of this would be large spruce trees converting grass blocks into podzol.
 * <p>
 * This event is not {@linkplain Cancelable cancellable}.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}.
 */
public class AlterGroundEvent extends Event {
    private final LevelSimulatedReader level;
    private final RandomSource random;
    private final BlockPos pos;
    private final BlockState originalAltered;
    private BlockState newAltered;

    @ApiStatus.Internal
    public AlterGroundEvent(LevelSimulatedReader level, RandomSource random, BlockPos pos, BlockState altered) {
        super();
        this.level = level;
        this.random = random;
        this.pos = pos;
        this.originalAltered = altered;
        this.newAltered = altered;
    }

    public LevelSimulatedReader getLevel() {
        return this.level;
    }

    public RandomSource getRandom() {
        return this.random;
    }

    /**
     * {@return the position of the block that will be altered}
     */
    public BlockPos getPos() {
        return this.pos;
    }

    /**
     * {@return the original block state that would be placed by the ground decorator}
     */
    public BlockState getOriginalAlteredState() {
        return this.originalAltered;
    }

    /**
     * {@return the new block state to be placed by the ground decorator}
     */
    public BlockState getNewAlteredState() {
        return this.newAltered;
    }

    /**
     * @param newAltered the new block state to be placed by the ground decorator
     */
    public void setNewAlteredState(BlockState newAltered) {
        this.newAltered = newAltered;
    }
}