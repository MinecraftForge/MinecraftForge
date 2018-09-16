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

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLivingEvent<T extends EntityLivingBase> extends net.minecraftforge.eventbus.api.Event
{
    private final EntityLivingBase entity;
    private final RenderLivingBase<T> renderer;
    private final float partialRenderTick;
    private final double x;
    private final double y;
    private final double z;

    @Deprecated
    public RenderLivingEvent(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z)
    {
        this(entity, renderer, 1, x, y, z);
    }

    public RenderLivingEvent(EntityLivingBase entity, RenderLivingBase<T> renderer, float partialRenderTick, double x, double y, double z)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityLivingBase getEntity() { return entity; }
    public RenderLivingBase<T> getRenderer() { return renderer; }
    public float getPartialRenderTick() { return partialRenderTick; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    @net.minecraftforge.eventbus.api.Cancelable
    public static class Pre<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        @Deprecated
        public Pre(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        public Pre(EntityLivingBase entity, RenderLivingBase<T> renderer, float partialRenderTick, double x, double y, double z){ super(entity, renderer, partialRenderTick, x, y, z); }
    }
    public static class Post<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        @Deprecated
        public Post(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        public Post(EntityLivingBase entity, RenderLivingBase<T> renderer, float partialRenderTick, double x, double y, double z){ super(entity, renderer, partialRenderTick, x, y, z); }
    }

    public abstract static class Specials<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Specials(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, 0, x, y, z); }

        @net.minecraftforge.eventbus.api.Cancelable
        public static class Pre<T extends EntityLivingBase> extends Specials<T>
        {
            public Pre(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
        public static class Post<T extends EntityLivingBase> extends Specials<T>
        {
            public Post(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer,  x, y, z); }
        }
    }
}
