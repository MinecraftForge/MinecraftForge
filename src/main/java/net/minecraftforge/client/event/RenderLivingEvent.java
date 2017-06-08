/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLivingEvent<T extends EntityLivingBase> extends Event
{
    private final EntityLivingBase entity;
    private final RenderLivingBase<T> renderer;
    private final double x;
    private final double y;
    private final double z;

    public RenderLivingEvent(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityLivingBase getEntity() { return entity; }
    public RenderLivingBase<T> getRenderer() { return renderer; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    @Cancelable
    public static class Pre<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Pre(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }
    public static class Post<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Post(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }

    public abstract static class Specials<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Specials(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }

        @Cancelable
        public static class Pre<T extends EntityLivingBase> extends Specials<T>
        {
            public Pre(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
        public static class Post<T extends EntityLivingBase> extends Specials<T>
        {
            public Post(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
    }
    
    /**
     * Called for each layer renderer that's rendered on an entity.
     */
    public abstract static class Layers<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        private LayerRenderer<T> layerRenderer;
        
        protected Layers(EntityLivingBase entity, RenderLivingBase<T> entityRenderer, LayerRenderer<T> layerRenderer, double x, double y, double z)
        {
            super(entity, entityRenderer, x, y, z);
            this.layerRenderer = layerRenderer;
        }

        @Cancelable
        public static class Pre<T extends EntityLivingBase> extends Layers<T>
        {
            public Pre(EntityLivingBase entity, RenderLivingBase<T> entityRenderer, LayerRenderer<T> layerRenderer, double x, double y, double z)
            {
                super(entity, entityRenderer, layerRenderer, x, y, z);
            }
        }
        
        public static class Post<T extends EntityLivingBase> extends Layers<T>
        {
            public Post(EntityLivingBase entity, RenderLivingBase<T> entityRenderer, LayerRenderer<T> layerRenderer, double x, double y, double z)
            {
                super(entity, entityRenderer, layerRenderer, x, y, z);
            }
        }
        
        public LayerRenderer<T> getLayerRenderer()
        {
            return this.layerRenderer;
        }
    }
}
