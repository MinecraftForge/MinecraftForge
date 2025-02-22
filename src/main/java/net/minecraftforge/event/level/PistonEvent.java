/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.Nullable;

/**
 * Base piston event, use {@link PistonEvent.Post} and {@link PistonEvent.Pre}
 */
public sealed interface PistonEvent extends BlockEvent {
    enum PistonMoveType {
        EXTEND(true), RETRACT(false);

        public final boolean isExtend;

        PistonMoveType(boolean isExtend)
        {
            this.isExtend = isExtend;
        }
    }

    /**
     * @return The direction of the piston block
     */
    Direction direction();

    /**
     * Helper method that gets the piston position offset by its facing
     */
    default BlockPos faceOffsetPos() {
        return pos().relative(direction());
    }

    /**
     * @return The movement type of the piston (extension, retraction)
     */
    PistonMoveType pistonMoveType();

    /**
     * @return A piston structure helper for this movement. Returns null if the world stored is not a {@link Level}
     */
    @Nullable
    default PistonStructureResolver structureHelper() {
        if (level() instanceof Level) {
            return new PistonStructureResolver((Level) level(), pos(), direction(), pistonMoveType().isExtend);
        } else {
            return null;
        }
    }

    /**
     * Fires after the piston has moved and set surrounding states. This will not fire if {@link PistonEvent.Pre} is cancelled.
     */
    record Post(
            Level level,
            BlockPos pos,
            BlockState state,
            Direction direction,
            PistonMoveType pistonMoveType
    ) implements PistonEvent, RecordEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);

        public Post(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            this(world, pos, world.getBlockState(pos), direction, moveType);
        }
    }

    /**
     * Fires before the piston has updated block states. Cancellation prevents movement.
     */
    record Pre(
            Level level,
            BlockPos pos,
            BlockState state,
            Direction direction,
            PistonMoveType pistonMoveType
    ) implements Cancellable, PistonEvent, RecordEvent {
        public static final EventBus<Pre> BUS = EventBus.create(Pre.class);

        public Pre(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            this(world, pos, world.getBlockState(pos), direction, moveType);
        }
    }
}
