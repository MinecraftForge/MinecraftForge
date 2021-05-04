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
import net.minecraftforge.client.event.RenderWorldEvent.RenderWorldTerrainUpdateEvent;
import net.minecraftforge.client.event.RenderWorldEvent.RenderWorldBlockLayerEvent;
import net.minecraftforge.client.event.RenderWorldEvent.RenderWorldRenderTypeFinishEvent;
import net.minecraftforge.common.MinecraftForge;

public final class RenderWorldEventHandler
{
    private static boolean isRenderWorldPhase = false;

    // context fields
    private static WorldRenderer worldRenderer;
    private static MatrixStack matrixStack;
    private static float partialTicks;
    private static ClippingHelper clippingHelper;
    private static ActiveRenderInfo activeRenderInfo;

    private RenderWorldEventHandler() {}

    public static void startWorldRenderPhase(final WorldRenderer worldRendererIn,
        final MatrixStack matrixStackIn,
        final float partialTicksIn,
        final ClippingHelper clippingHelperIn,
        final ActiveRenderInfo activeRenderInfoIn)
    {
        isRenderWorldPhase = true;
        worldRenderer = worldRendererIn;
        matrixStack = matrixStackIn;
        partialTicks = partialTicksIn;
        clippingHelper = clippingHelperIn;
        activeRenderInfo = activeRenderInfoIn;
    }

    public static void endWorldRenderPhase()
    {
        isRenderWorldPhase = false;
    }

    public static void fireTerrainUpdate(final long finishTimeNano)
    {
        if (!isRenderWorldPhase)
            return;

        isRenderWorldPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderWorldTerrainUpdateEvent(worldRenderer, matrixStack, partialTicks, clippingHelper, activeRenderInfo, finishTimeNano));
        isRenderWorldPhase = true;
    }

    public static void fireBlockLayer(final RenderType renderType)
    {
        if (!isRenderWorldPhase)
            return;

        isRenderWorldPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderWorldBlockLayerEvent(worldRenderer, matrixStack, partialTicks, clippingHelper, activeRenderInfo, renderType));
        isRenderWorldPhase = true;
    }

    public static void fireRenderTypeFinish(final RenderType renderType, final BufferBuilder bufferBuilder)
    {
        if (!isRenderWorldPhase)
            return;

        isRenderWorldPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderWorldRenderTypeFinishEvent(worldRenderer, matrixStack, partialTicks, clippingHelper, activeRenderInfo, renderType, bufferBuilder));
        isRenderWorldPhase = true;
    }
}