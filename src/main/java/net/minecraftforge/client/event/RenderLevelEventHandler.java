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

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;

public final class RenderLevelEventHandler {
    private static boolean isRenderLevelPhase = false;
    
    // context fields
    private static LevelRenderer levelRenderer;
    private static PoseStack poseStack;
    private static float partialTicks;
    private static Frustum frustum;
    private static Camera camera;
    private static long startTimeNano;
    private static Matrix4f projectionMatrix;
    
    private RenderLevelEventHandler() {
    }
    
    public static void startLevelRenderPhase(final LevelRenderer levelRendererIn,
                                             final PoseStack poseStackIn,
                                             final float partialTicksIn,
                                             final Frustum frustumIn,
                                             final Camera cameraIn,
                                             final long startTimeNanoIn,
                                             final Matrix4f projectionMatrixIn,
                                             ProfilerFiller profilerfiller) {
        profilerfiller.popPush("forge_render_level_begin");
    
        levelRenderer = levelRendererIn;
        poseStack = poseStackIn;
        partialTicks = partialTicksIn;
        frustum = frustumIn;
        camera = cameraIn;
        startTimeNano = startTimeNanoIn;
        projectionMatrix = projectionMatrixIn;
        
        MinecraftForge.EVENT_BUS.post(new RenderLevelEvent.BeginEvent(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix));
        
        isRenderLevelPhase = true;
    }
    
    public static void endLevelRenderPhase(ProfilerFiller profilerfiller) {
        profilerfiller.popPush("forge_render_level_end");
    
        isRenderLevelPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderLevelEvent.EndEvent(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix));
    }
    
    public static void fireTerrainUpdate(long finishTimeNano, ProfilerFiller profilerfiller) {
        if (!isRenderLevelPhase) {
            return;
        }
    
        profilerfiller.popPush("forge_render_level_terrain_update");
    
        isRenderLevelPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderLevelEvent.TerrainUpdateEvent(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix, finishTimeNano));
        isRenderLevelPhase = true;
    }
    
    public static void fireBlockLayer(final RenderType renderType, ProfilerFiller profilerfiller) {
        if (!isRenderLevelPhase) {
            return;
        }
    
        profilerfiller.popPush("forge_render_level_block_layer");
    
        isRenderLevelPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderLevelEvent.BlockLayerEvent(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix, renderType));
        isRenderLevelPhase = true;
    }
    
    public static void fireRenderTypeFinish(final RenderType renderType, final BufferBuilder bufferBuilder) {
        if (!isRenderLevelPhase) {
            return;
        }
        
        isRenderLevelPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderLevelEvent.RenderTypeFinishEvent(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix, renderType, bufferBuilder));
        isRenderLevelPhase = true;
    }
    
    public static void fireTerrainAndEntitiesDone(ProfilerFiller profilerfiller) {
        if (!isRenderLevelPhase) {
            return;
        }
    
        profilerfiller.popPush("forge_render_level_terrain_and_entities");
    
        isRenderLevelPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderLevelEvent.TerrainAndEntitiesDoneEvent(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix));
        isRenderLevelPhase = true;
    }
    
    public static void fireWeatherDone(ProfilerFiller profilerfiller) {
        if (!isRenderLevelPhase) {
            return;
        }
    
        profilerfiller.popPush("forge_render_level_weather");
    
        isRenderLevelPhase = false;
        MinecraftForge.EVENT_BUS.post(new RenderLevelEvent.WeatherDoneEvent(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix));
        isRenderLevelPhase = true;
    }
} 