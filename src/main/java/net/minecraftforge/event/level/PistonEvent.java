/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.level;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Base piston event, use {@link PistonEvent.Post} and {@link PistonEvent.Pre}
 */
@NullMarked
public sealed interface PistonEvent extends BlockEvent {
    /**
     * @return The position of the piston
     */
    @Override
    BlockPos getPos();

    /**
     * @return The move direction of the piston
     */
    Direction getDirection();

    /**
     * Helper method that gets the piston position offset by its facing
     */
    default BlockPos getFaceOffsetPos() {
        return this.getPos().relative(getDirection());
    }

    /**
     * @return The movement type of the piston (extension, retraction)
     */
    PistonMoveType getPistonMoveType();

    /**
     * @return A piston structure helper for this movement. Returns null if the world stored is not a {@link Level}
     */
    @Nullable
    default PistonStructureResolver getStructureHelper() {
        if (getLevel() instanceof Level) {
            return new PistonStructureResolver((Level) getLevel(), getPos(), getDirection(), getPistonMoveType().isExtend());
        } else {
            return null;
        }
    }

    /**
     * Fires before the piston has updated block states. Cancellation prevents movement.
     */
    record Pre(
            LevelAccessor getLevel,
            BlockPos getPos,
            BlockState getState,
            Direction getDirection,
            PistonMoveType getPistonMoveType
    ) implements Cancellable, PistonEvent, RecordEvent {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);

        public Pre(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            this(world, pos, world.getBlockState(pos), direction, moveType);
        }
    }

    /**
     * Fires after the piston has moved and set surrounding states. This will not fire if {@link PistonEvent.Pre} is cancelled.
     */
    record Post(
            LevelAccessor getLevel,
            BlockPos getPos,
            BlockState getState,
            Direction getDirection,
            PistonMoveType getPistonMoveType
    ) implements RecordEvent, PistonEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);

        public Post(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            this(world, pos, world.getBlockState(pos), direction, moveType);
        }
    }

    enum PistonMoveType {
        EXTEND {
            @Override
            public final boolean isExtend() {
                return true;
            }
        },
        RETRACT;

        public boolean isExtend() {
            return false;
        }
    }
}
