/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraftforge.eventbus.api.Event;

/**
 * Abstract class for all world rendering events.
 * <br>
 * These events are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class RenderWorldEvent extends Event
{
    protected final WorldRenderer worldRenderer;
    protected final MatrixStack matrixStack;
    protected final float partialTicks;
    protected final ClippingHelper clippingHelper;
    protected final ActiveRenderInfo activeRenderInfo;

    protected RenderWorldEvent(final WorldRenderer worldRenderer,
        final MatrixStack matrixStack,
        final float partialTicks,
        final ClippingHelper clippingHelper,
        final ActiveRenderInfo activeRenderInfo)
    {
        this.worldRenderer = worldRenderer;
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
        this.clippingHelper = clippingHelper;
        this.activeRenderInfo = activeRenderInfo;
    }

    public WorldRenderer getWorldRenderer()
    {
        return worldRenderer;
    }

    public MatrixStack getMatrixStack()
    {
        return matrixStack;
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

    /**
     * Fired after sky rendering, can be used to check which parts of terrain needs updating.
     * <br>
     * If {@link System#nanoTime} is greater than <code>finishTimeNano</code> then you should quit this event immediately.
     */
    public static class RenderWorldTerrainUpdateEvent extends RenderWorldEvent
    {
        protected final long finishTimeNano;

        public RenderWorldTerrainUpdateEvent(final WorldRenderer worldRenderer,
            final MatrixStack matrixStack,
            final float partialTicks,
            final ClippingHelper clippingHelper,
            final ActiveRenderInfo activeRenderInfo,
            final long finishTimeNano)
        {
            super(worldRenderer, matrixStack, partialTicks, clippingHelper, activeRenderInfo);
            this.finishTimeNano = finishTimeNano;
        }

        public long getFinishTimeNano()
        {
            return finishTimeNano;
        }
    }

    /**
     * Fired for each block layer being rendered. After vanilla block layer rendering, render type already set up.
     */
    public static class RenderWorldBlockLayerEvent extends RenderWorldEvent
    {
        protected final RenderType renderType;

        public RenderWorldBlockLayerEvent(final WorldRenderer worldRenderer,
            final MatrixStack matrixStack,
            final float partialTicks,
            final ClippingHelper clippingHelper,
            final ActiveRenderInfo activeRenderInfo,
            final RenderType renderType)
        {
            super(worldRenderer, matrixStack, partialTicks, clippingHelper, activeRenderInfo);
            this.renderType = renderType;
        }

        public RenderType getRenderType()
        {
            return renderType;
        }
    }

    /**
     * Fired right before finishing the buffer for given render type. Use the buffer builder.
     */
    public static class RenderWorldRenderTypeFinishEvent extends RenderWorldEvent
    {
        protected final RenderType renderType;
        protected final BufferBuilder bufferBuilder;

        public RenderWorldRenderTypeFinishEvent(final WorldRenderer worldRenderer,
            final MatrixStack matrixStack,
            final float partialTicks,
            final ClippingHelper clippingHelper,
            final ActiveRenderInfo activeRenderInfo,
            final RenderType renderType,
            final BufferBuilder bufferBuilder)
        {
            super(worldRenderer, matrixStack, partialTicks, clippingHelper, activeRenderInfo);
            this.renderType = renderType;
            this.bufferBuilder = bufferBuilder;
        }

        public RenderType getRenderType()
        {
            return renderType;
        }

        public BufferBuilder getBufferBuilder()
        {
            return bufferBuilder;
        }
    }
}