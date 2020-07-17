/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.math.vector.Matrix4f;

public abstract class RenderWorldEvent extends net.minecraftforge.eventbus.api.Event
{
    private final WorldRenderer context;
    private final MatrixStack mStack;
    private final float partialTicks;
    private final ClippingHelper clippinghelper;
    private final ActiveRenderInfo activeRenderInfoIn;
    private final RenderTypeBuffers renderTypeBuffers;

    private RenderWorldEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippinghelper, ActiveRenderInfo activeRenderInfoIn, RenderTypeBuffers renderTypeBuffers)
    {
        this.context = context;
        this.mStack = mStack;
        this.partialTicks = partialTicks;
        this.clippinghelper = clippinghelper;
        this.activeRenderInfoIn = activeRenderInfoIn;
        this.renderTypeBuffers = renderTypeBuffers;
    }

    public WorldRenderer getContext()
    {
        return context;
    }

    public MatrixStack getMatrixStack()
    {
        return mStack;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public ClippingHelper getClippinghelper()
    {
        return clippinghelper;
    }

    public ActiveRenderInfo getActiveRenderInfoIn()
    {
        return activeRenderInfoIn;
    }

    public RenderTypeBuffers getRenderTypeBuffers()
    {
        return renderTypeBuffers;
    }

    public static class RenderWorldTerrainUpdateEvent extends RenderWorldEvent
    {
        private final long finishTimeNano;

        public RenderWorldTerrainUpdateEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippinghelper, ActiveRenderInfo activeRenderInfoIn, RenderTypeBuffers renderTypeBuffers, long finishTimeNano)
        {
            super(context, mStack, partialTicks, clippinghelper, activeRenderInfoIn, renderTypeBuffers);
            this.finishTimeNano = finishTimeNano;
        }

        public long getFinishTimeNano()
        {
            return finishTimeNano;
        }
    }

    public static class RenderWorldBlockLayerEvent extends RenderWorldEvent
    {
        private final RenderType blockLayer;

        public RenderWorldBlockLayerEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippinghelper, ActiveRenderInfo activeRenderInfoIn, RenderTypeBuffers renderTypeBuffers, RenderType blockLayer)
        {
            super(context, mStack, partialTicks, clippinghelper, activeRenderInfoIn, renderTypeBuffers);
            this.blockLayer = blockLayer;
        }

        public RenderType getBlockLayer()
        {
            return blockLayer;
        }
    }

    public static class RenderWorldEntitiesEvent extends RenderWorldEvent
    {
        public RenderWorldEntitiesEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippinghelper, ActiveRenderInfo activeRenderInfoIn, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, partialTicks, clippinghelper, activeRenderInfoIn, renderTypeBuffers);
        }
    }

    public static class RenderWorldTileEntitiesEvent extends RenderWorldEvent
    {
        public RenderWorldTileEntitiesEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippinghelper, ActiveRenderInfo activeRenderInfoIn, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, partialTicks, clippinghelper, activeRenderInfoIn, renderTypeBuffers);
        }
    }

    public static class RenderWorldLastEvent extends RenderWorldEvent
    {
        public RenderWorldLastEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippinghelper, ActiveRenderInfo activeRenderInfoIn, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, partialTicks, clippinghelper, activeRenderInfoIn, renderTypeBuffers);
        }
    }
}
