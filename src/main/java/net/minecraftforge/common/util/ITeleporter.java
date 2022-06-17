/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.function.Function;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for handling the placement of entities during dimension change.
 * <p>
 * An implementation of this interface can be used to place the entity
 * in a safe location, or generate a return portal, for instance.
 * <p>
 * See the {@link PortalForcer} class, which has
 * been patched to implement this interface, for a vanilla example.
 */
public interface ITeleporter
{
    /**
     * Called to handle placing the entity in the new world.
     * <p>
     * The initial position of the entity will be its
     * position in the origin world, multiplied horizontally
     * by the computed cross-dimensional movement factor.
     * <p>
     * Note that the supplied entity has not yet been spawned
     * in the destination world at the time.
     *
     * @param entity the entity to be placed
     * @param currentWorld the entity's origin
     * @param destWorld the entity's destination
     * @param yaw the suggested yaw value to apply
     * @param repositionEntity a function to reposition the entity, which returns the new entity in the new dimension. This is the vanilla implementation of the dimension travel logic. If the supplied boolean is true, it is attempted to spawn a new portal.
     *
     * @return the entity in the new World. Vanilla creates for most {@link Entity}s a new instance and copy the data. But <b>you are not allowed</b> to create a new instance for {@link Player}s! Move the player and update its state, see {@link ServerPlayer#changeDimension(ServerLevel, ITeleporter)}
     */
    default Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
        return repositionEntity.apply(true);
    }

    /**
     * Gets the PortalInfo. defaultPortalInfo references the
     * vanilla code and should not be used for your purposes.
     * Override this method to handle your own logic.
     * <p>
     * Return {@code null} to prevent teleporting.
     *
     * @param entity The entity teleporting before the teleport
     * @param destWorld The world the entity is teleporting to
     * @param defaultPortalInfo A reference to the vanilla method for getting portal info. You should implement your own logic instead of using this
     *
     * @return The location, rotation, and motion of the entity in the destWorld after the teleport
     */
    @Nullable
    default PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo)
    {
        return this.isVanilla() ? defaultPortalInfo.apply(destWorld) : new PortalInfo(entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }

    /**
     * Is this teleporter the vanilla instance.
     */
    default boolean isVanilla()
    {
        return this.getClass() == PortalForcer.class;
    }

    /**
     * Called when vanilla wants to play the portal sound after teleporting. Return true to play the vanilla sound.
     * @param player the player
     * @param sourceWorld the source world
     * @param destWorld the target world
     * @return true to play the vanilla sound
     */
    default boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld)
    {
        return true;
    }

}
