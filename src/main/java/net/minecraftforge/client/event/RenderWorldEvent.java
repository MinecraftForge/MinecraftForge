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
import net.minecraftforge.common.MinecraftForge;

/**
 * Abstract class for all world rendering events.
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class RenderWorldEvent extends net.minecraftforge.eventbus.api.Event
{
    private final WorldRenderer context;
    private final MatrixStack mStack;
    private final RenderTypeBuffers renderTypeBuffers;

    private RenderWorldEvent(WorldRenderer context, MatrixStack mStack, RenderTypeBuffers renderTypeBuffers)
    {
        this.context = context;
        this.mStack = mStack;
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

    public RenderTypeBuffers getRenderTypeBuffers()
    {
        return renderTypeBuffers;
    }

    public static class InnerRenderWorldEvent extends RenderWorldEvent
    {
        private final float partialTicks;
        private final ClippingHelper clippingHelper;
        private final ActiveRenderInfo activeRenderInfo;

        private InnerRenderWorldEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippingHelper, ActiveRenderInfo activeRenderInfo, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, renderTypeBuffers);
            this.partialTicks = partialTicks;
            this.clippingHelper = clippingHelper;
            this.activeRenderInfo = activeRenderInfo;
        }

        public float getPartialTicks()
        {
            return partialTicks;
        }
    
        public ClippingHelper getClippingHelper()
        {
            return clippingHelper;
        }
    
        public ActiveRenderInfo getActiveRenderInfo()
        {
            return activeRenderInfo;
        }
    }

    /**
     * Event fired before first rendering call. Can be used to check which terrain needs updating.
     * <br>
     * If {@link System#nanoTime} is greater than <code>finishTimeNano</code> then you should quit this event immediately.
     */
    public static class RenderWorldTerrainUpdateEvent extends InnerRenderWorldEvent
    {
        private final long finishTimeNano;

        public RenderWorldTerrainUpdateEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippingHelper, ActiveRenderInfo activeRenderInfo, RenderTypeBuffers renderTypeBuffers, long finishTimeNano)
        {
            super(context, mStack, partialTicks, clippingHelper, activeRenderInfo, renderTypeBuffers);
            this.finishTimeNano = finishTimeNano;
        }

        public long getFinishTimeNano()
        {
            return finishTimeNano;
        }
    }

    /**
     * Event fired for each block layer being rendered.
     */
    public static class RenderWorldBlockLayerEvent extends RenderWorldEvent
    {
        private final RenderType blockLayer;
        private final double cameraX;
        private final double cameraY;
        private final double cameraZ;

        public RenderWorldBlockLayerEvent(WorldRenderer context, MatrixStack mStack, RenderTypeBuffers renderTypeBuffers, RenderType blockLayer, double cameraX, double cameraY, double cameraZ)
        {
            super(context, mStack, renderTypeBuffers);
            this.blockLayer = blockLayer;
            this.cameraX = cameraX;
            this.cameraY = cameraY;
            this.cameraZ = cameraZ;
        }

        public RenderType getBlockLayer()
        {
            return blockLayer;
        }

        public double getCameraX()
        {
            return cameraX;
        }

        public double getCameraY()
        {
            return cameraY;
        }

        public double getCameraZ()
        {
            return cameraZ;
        }
    }

    /**
     * Event fired before end of entity rendering.
     */
    public static class RenderWorldEntitiesEvent extends InnerRenderWorldEvent
    {
        public RenderWorldEntitiesEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippingHelper, ActiveRenderInfo activeRenderInfo, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, partialTicks, clippingHelper, activeRenderInfo, renderTypeBuffers);
        }
    }

    /**
     * Event fired before end of tile entity rendering.
     */
    public static class RenderWorldTileEntitiesEvent extends InnerRenderWorldEvent
    {
        public RenderWorldTileEntitiesEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippingHelper, ActiveRenderInfo activeRenderInfo, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, partialTicks, clippingHelper, activeRenderInfo, renderTypeBuffers);
        }
    }

    /**
     * Event fired before weather and clouds are rendered.
     */
    public static class RenderWorldPreWeatherEvent extends InnerRenderWorldEvent
    {
        public RenderWorldPreWeatherEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippingHelper, ActiveRenderInfo activeRenderInfo, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, partialTicks, clippingHelper, activeRenderInfo, renderTypeBuffers);
        }
    }

    /**
     * Event fired after entire World is rendered.
     */
    public static class RenderWorldLastEvent extends InnerRenderWorldEvent
    {
        public RenderWorldLastEvent(WorldRenderer context, MatrixStack mStack, float partialTicks, ClippingHelper clippingHelper, ActiveRenderInfo activeRenderInfo, RenderTypeBuffers renderTypeBuffers)
        {
            super(context, mStack, partialTicks, clippingHelper, activeRenderInfo, renderTypeBuffers);
        }
    }
}
