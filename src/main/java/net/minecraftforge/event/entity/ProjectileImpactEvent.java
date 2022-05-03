/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when a projectile entity impacts something.
 * This event is fired via {@link ForgeEventFactory#onProjectileImpact(Entity, RayTraceResult)}
 * Subclasses of this event exist for more specific types of projectile.
 * This event is fired for all vanilla projectiles by Forge,
 * custom projectiles should fire this event and check the result in a similar fashion.
 * This event is cancelable. When canceled, the impact will not be processed.
 * Killing or other handling of the entity after event cancellation is up to the modder.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class ProjectileImpactEvent extends EntityEvent
{
    private final RayTraceResult ray;

    public ProjectileImpactEvent(Entity entity, RayTraceResult ray)
    {
        super(entity);
        this.ray = ray;
    }

    public RayTraceResult getRayTraceResult()
    {
        return ray;
    }

    @Cancelable
    public static class Arrow extends ProjectileImpactEvent
    {
        private final AbstractArrowEntity arrow;

        public Arrow(AbstractArrowEntity arrow, RayTraceResult ray)
        {
            super(arrow, ray);
            this.arrow = arrow;
        }

        public AbstractArrowEntity getArrow()
        {
            return arrow;
        }
    }

    @Cancelable
    public static class Fireball extends ProjectileImpactEvent
    {
        private final DamagingProjectileEntity fireball;

        public Fireball(DamagingProjectileEntity fireball, RayTraceResult ray)
        {
            super(fireball, ray);
            this.fireball = fireball;
        }

        public DamagingProjectileEntity getFireball()
        {
            return fireball;
        }
    }

    @Cancelable
    public static class Throwable extends ProjectileImpactEvent
    {
        private final ThrowableEntity throwable;

        public Throwable(ThrowableEntity throwable, RayTraceResult ray)
        {
            super(throwable, ray);
            this.throwable = throwable;
        }

        public ThrowableEntity getThrowable()
        {
            return throwable;
        }
    }

    /**
     * Event is cancellable, causes firework to ignore the current hit and continue on its journey.
     */
    @Cancelable
    public static class FireworkRocket extends ProjectileImpactEvent
    {
        private final FireworkRocketEntity fireworkRocket;

        public FireworkRocket(FireworkRocketEntity fireworkRocket, RayTraceResult ray)
        {
            super(fireworkRocket, ray);
            this.fireworkRocket = fireworkRocket;
        }

        public FireworkRocketEntity getFireworkRocket()
        {
            return fireworkRocket;
        }
    }

    /**
     * Event is cancellable, causes bobber to ignore the current hit and continue on its journey.
     */
    @Cancelable
    public static class FishingBobber extends ProjectileImpactEvent
    {
        private final FishingBobberEntity fishingBobber;

        public FishingBobber(FishingBobberEntity fishingBobber, RayTraceResult ray)
        {
            super(fishingBobber, ray);
            this.fishingBobber = fishingBobber;
        }

        public FishingBobberEntity getFishingBobber()
        {
            return fishingBobber;
        }
    }
}
