/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public interface IForgePlayer
{

    private Player self()
    {
        return (Player) this;
    }

    /**
     * The attack range is increased by 3 for creative players, unless it is currently zero, which disables attacks.
     * @return The attack range of this player.
     */
    default double getAttackRange()
    {
        double range = self().getAttributeValue(ForgeMod.ATTACK_RANGE.get());
        return range == 0 ? 0 : range + (self().isCreative() ? 3 : 0);
    }

    /**
     * The reach distance is increased by 0.5 for creative players, unless it is currently zero, which disables interactions.
     * @return The reach distance of this player.
     */
    default double getReachDistance()
    {
        double reach = self().getAttributeValue(ForgeMod.REACH_DISTANCE.get());
        return reach == 0 ? 0 : reach + (self().isCreative() ? 0.5 : 0);
    }

    /**
     * Checks if the player can attack the passed entity.<br>
     * On the server, additional leniency is added to account for movement/lag.
     * @param entity The entity being range-checked.
     * @param padding Extra validation distance.
     * @return If the player can attack the passed entity.
     */
    default boolean canHit(Entity entity, double padding) // Do not rename to canAttack - will conflict with LivingEntity#canAttack
    {
        return isCloseEnough(entity, getAttackRange() + padding);
    }

    /**
     * Checks if the player can reach (right-click) the passed entity.<br>
     * On the server, additional leniency is added to account for movement/lag.
     * @param entity The entity being range-checked.
     * @param padding Extra validation distance.
     * @return If the player can interact with the passed entity.
     */
    default boolean canInteractWith(Entity entity, double padding)
    {
        return isCloseEnough(entity, getReachDistance() + padding);
    }

    /**
     * Utility check to see if the player is close enough to a target entity.
     * @param entity The entity being checked against
     * @param dist The max distance allowed
     * @return If the eye-to-center distance between this player and the passed entity is less than dist.
     */
    default boolean isCloseEnough(Entity entity, double dist)
    {
        Vec3 eye = self().getEyePosition();
        Vec3 targetCenter = entity.getPosition(1.0F).add(0, entity.getBbHeight() / 2, 0);
        Optional<Vec3> hit = entity.getBoundingBox().clip(eye, targetCenter); //This hit should almost always be present, but we have a fallback just in case.  It likely indicates lag if it is not present.
        return (hit.isPresent() ? eye.distanceToSqr(hit.get()) : self().distanceToSqr(entity)) < dist * dist;
    }

    /**
     * Checks if the player can reach (right-click) a block.<br>
     * On the server, additional leniency is added to account for movement/lag.
     * @param pos The position being range-checked.
     * @param padding Extra validation distance.
     * @return If the player can interact with this location.
     */
    default boolean canInteractWith(BlockPos pos, double padding)
    {
        double reach = getReachDistance() + padding;
        return self().getEyePosition().distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) < reach * reach;
    }

}
