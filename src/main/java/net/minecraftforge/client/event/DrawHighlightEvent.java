/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event called whenever the selection highlight around blocks is about to be rendered.
 * Canceling this event stops the selection highlight from being rendered.
 */
@Cancelable
public class DrawHighlightEvent extends Event
{
    private final WorldRenderer context;
    private final ActiveRenderInfo info;
    private final int subID;
    private final float partialTicks;

    public DrawHighlightEvent(WorldRenderer context, ActiveRenderInfo info, int subID, float partialTicks)
    {
        this.context = context;
        this.info = info;
        this.subID = subID;
        this.partialTicks= partialTicks;
    }

    public WorldRenderer getContext() { return context; }
    public ActiveRenderInfo getInfo() { return info; }
    public int getSubID() { return subID; }
    public float getPartialTicks() { return partialTicks; }

    /**
     * A variant of the DrawHighlightEvent only called when a block is highlighted.
     */
    @Cancelable
    public static class HighlightBlock extends DrawHighlightEvent
    {
        private BlockRayTraceResult target;
        public HighlightBlock(WorldRenderer context, ActiveRenderInfo info, int subID, float partialTicks, BlockRayTraceResult target)
        {
            super(context, info, subID, partialTicks);
            this.target = target;
        }

        public BlockRayTraceResult getTarget()
        {
            return target;
        }
    }

    /**
     * A variant of the DrawHighlightEvent only called when an entity is highlighted.
     * Canceling this event has no effect.
     */
    @Cancelable
    public static class HighlightEntity extends DrawHighlightEvent
    {
        private EntityRayTraceResult target;
        public HighlightEntity(WorldRenderer context, ActiveRenderInfo info, int subID, float partialTicks, EntityRayTraceResult target)
        {
            super(context, info, subID, partialTicks);
            this.target = target;
        }

        public EntityRayTraceResult getTarget()
        {
            return target;
        }
    }

    /**
     * A variant of the DrawHighlightEvent only called when nothing is highlighted.
     * Canceling this event has no effect.
     */
    @Cancelable
    public static class HighlightNothing extends DrawHighlightEvent
    {
        private RayTraceResult target;
        public HighlightNothing(WorldRenderer context, ActiveRenderInfo info, int subID, float partialTicks, RayTraceResult target)
        {
            super(context, info, subID, partialTicks);
            this.target = target;
        }

        public RayTraceResult getTarget()
        {
            return target;
        }
    }
}
