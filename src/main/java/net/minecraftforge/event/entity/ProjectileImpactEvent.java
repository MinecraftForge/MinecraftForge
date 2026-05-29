/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.event.singularityEventFactory;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * This event is fired when a projectile entity impacts something.<br>
 * This event is fired via {@link singularityEventFactory#onProjectileImpact(Projectile, HitResult)}
 * This event is fired for all vanilla projectiles by singularity,
 * custom projectiles should fire this event and check the result in a similar fashion.
 * You can also directly set the {@link ImpactResult} to change the impact behaviour.
 * @see #setImpactResult(ImpactResult)
 */
public final class ProjectileImpactEvent extends MutableEvent implements EntityEvent {
    public static final EventBus<ProjectileImpactEvent> BUS = EventBus.create(ProjectileImpactEvent.class);

    private final HitResult ray;
    private final Projectile projectile;

    private ImpactResult result = ImpactResult.DEFAULT;

    public ProjectileImpactEvent(Projectile projectile, HitResult ray) {
        this.ray = ray;
        this.projectile = projectile;
    }

    @Override
    public Entity getEntity() {
        return projectile;
    }

    public HitResult getRayTraceResult() {
        return ray;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public void setImpactResult(@NotNull ImpactResult newResult) {
        this.result = Objects.requireNonNull(newResult, "ImpactResult cannot be null");
    }

    public ImpactResult getImpactResult() {
        return result;
    }

    public enum ImpactResult {
        /**
         * The default behaviour, the projectile will be destroyed and the hit will be processed.
         */
        DEFAULT,
        /**
         * The projectile will pass through the current entity as if it wasn't there. This will return default behaviour if there is no entity.
         */
        SKIP_ENTITY,
        /**
         * Damage the entity and stop the projectile here, the projectile will not pierce.
         */
        STOP_AT_CURRENT,
        /**
         * Cancel the piercing aspect of the projectile, and do not damage the entity.
         */
        STOP_AT_CURRENT_NO_DAMAGE
    }
}
