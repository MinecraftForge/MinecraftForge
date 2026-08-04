/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.server.ServerWorld;

/**
 * Interface for handling the placement of entities during dimension change.
 *
 * An implementation of this interface can be used to place the entity
 * in a safe location, or generate a return portal, for instance.
 *
 * See the {@link net.minecraft.world.Teleporter} class, which has
 * been patched to implement this interface, for a vanilla example.
 */
public interface ITeleporter {

    /**
     * Called to handle placing the entity in the new world.
     *
     * The initial position of the entity will be its
     * position in the origin world, multiplied horizontally
     * by the computed cross-dimensional movement factor
     * (see {@link Dimension#getMovementFactor()}).
     *
     * Note that the supplied entity has not yet been spawned
     * in the destination world at the time.
     *
     * @param entity           the entity to be placed
     * @param currentWorld     the entity's origin
     * @param destWorld        the entity's destination
     * @param yaw              the suggested yaw value to apply
     * @param repositionEntity a function to reposition the entity, which returns the new entity in the new dimension. This is the vanilla implementation of the dimension travel logic. If the supplied boolean is true, it is attempted to spawn a new portal.
     * @return the entity in the new World. Vanilla creates for most {@link Entity}s a new instance and copy the data. But <b>you are not allowed</b> to create a new instance for {@link PlayerEntity}s! Move the player and update its state, see {@link ServerPlayerEntity#changeDimension(net.minecraft.world.dimension.DimensionType, ITeleporter)}
     */
    default Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
       return repositionEntity.apply(true);
    }
}
