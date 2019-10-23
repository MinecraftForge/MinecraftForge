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

import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;

/**
 * An event called whenever the selection highlight around blocks is about to be rendered.
 * Canceling this event stops the selection highlight from being rendered.
 */
//TODO: in 1.15 rename to DrawHighlightEvent
@Cancelable
public class DrawBlockHighlightEvent extends Event
{
    private final WorldRenderer context;
    private final ActiveRenderInfo info;
    private final RayTraceResult target;
    private final int subID;
    private final float partialTicks;

    public DrawBlockHighlightEvent(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, int subID, float partialTicks)
    {
        this.context = context;
        this.info = info;
        this.target = target;
        this.subID = subID;
        this.partialTicks= partialTicks;
    }

    public WorldRenderer getContext() { return context; }
    public ActiveRenderInfo getInfo() { return info; }
    public RayTraceResult getTarget() { return target; }
    public int getSubID() { return subID; }
    public float getPartialTicks() { return partialTicks; }

    /**
     * A variant of the DrawBlockHighlightEvent only called when a block is highlighted.
     */
    @Cancelable
    public static class HighlightBlock extends DrawBlockHighlightEvent
    {
        public HighlightBlock(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, int subID, float partialTicks)
        {
            super(context, info, target, subID, partialTicks);
        }

        @Override
        public BlockRayTraceResult getTarget()
        {
            return (BlockRayTraceResult) super.target;
        }
    }

    /**
     * A variant of the DrawBlockHighlightEvent only called when an entity is highlighted.
     * Canceling this event has no effect.
     */
    @Cancelable
    public static class HighlightEntity extends DrawBlockHighlightEvent
    {
        public HighlightEntity(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, int subID, float partialTicks)
        {
            super(context, info, target, subID, partialTicks);
        }

        @Override
        public EntityRayTraceResult getTarget()
        {
            return (EntityRayTraceResult) super.target;
        }
    }
}
