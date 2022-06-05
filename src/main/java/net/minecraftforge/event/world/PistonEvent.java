/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Base piston event, use {@link PistonEvent.Post} and {@link PistonEvent.Pre}
 */
public abstract class PistonEvent extends BlockEvent
{

    private final Direction direction;
    private final PistonMoveType moveType;

    /**
     * @param pos - The position of the piston
     * @param direction - The move direction of the piston
     */
    public PistonEvent(Level world, BlockPos pos, Direction direction, PistonMoveType moveType)
    {
        super(world, pos, world.getBlockState(pos));
        this.direction = direction;
        this.moveType = moveType;
    }

    /**
     * @return The direction of the piston block
     */
    public Direction getDirection()
    {
        return this.direction;
    }

    /**
     * Helper method that gets the piston position offset by its facing
     */
    public BlockPos getFaceOffsetPos()
    {
        return this.getPos().relative(direction);
    }

    /**
     * @return The movement type of the piston (extension, retraction)
     */
    public PistonMoveType getPistonMoveType()
    {
        return moveType;
    }

    /**
     * @return A piston structure helper for this movement. Returns null if the world stored is not a {@link Level}
     */
    @Nullable
    public PistonStructureResolver getStructureHelper()
    {
        if(this.getWorld() instanceof Level) {
            return new PistonStructureResolver((Level) this.getWorld(), this.getPos(), this.getDirection(), this.getPistonMoveType().isExtend);
        } else {
            return null;
        }
    }

    /**
     * Fires after the piston has moved and set surrounding states. This will not fire if {@link PistonEvent.Pre} is cancelled.
     */
    public static class Post extends PistonEvent
    {

        public Post(Level world, BlockPos pos, Direction direction, PistonMoveType moveType)
        {
            super(world, pos, direction, moveType);
        }

    }

    /**
     * Fires before the piston has updated block states. Cancellation prevents movement.
     */
    @Cancelable
    public static class Pre extends PistonEvent
    {

        public Pre(Level world, BlockPos pos, Direction direction, PistonMoveType moveType)
        {
            super(world, pos, direction, moveType);
        }

    }

    public static enum PistonMoveType
    {
        EXTEND(true), RETRACT(false);

        public final boolean isExtend;

        PistonMoveType(boolean isExtend)
        {
            this.isExtend = isExtend;
        }
    }

}
