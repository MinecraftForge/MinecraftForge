/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

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
        private final EntityArrow arrow;

        public Arrow(EntityArrow arrow, RayTraceResult ray)
        {
            super(arrow, ray);
            this.arrow = arrow;
        }

        public EntityArrow getArrow()
        {
            return arrow;
        }
    }

    @Cancelable
    public static class Fireball extends ProjectileImpactEvent
    {
        private final EntityFireball fireball;

        public Fireball(EntityFireball fireball, RayTraceResult ray)
        {
            super(fireball, ray);
            this.fireball = fireball;
        }

        public EntityFireball getFireball()
        {
            return fireball;
        }
    }

    @Cancelable
    public static class Throwable extends ProjectileImpactEvent
    {
        private final EntityThrowable throwable;

        public Throwable(EntityThrowable throwable, RayTraceResult ray)
        {
            super(throwable, ray);
            this.throwable = throwable;
        }

        public EntityThrowable getThrowable()
        {
            return throwable;
        }
    }
}
