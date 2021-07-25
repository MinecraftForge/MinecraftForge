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
import net.minecraftforge.eventbus.api.Event;

/**
 * Abstract class for all level rendering events.
 * <br>
 * These events are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
public abstract class RenderLevelEvent extends Event {
    protected final LevelRenderer levelRenderer;
    protected final PoseStack poseStack;
    protected final float partialTicks;
    protected final Frustum frustum;
    protected final Camera camera;
    protected final long startTimeNano;
    protected final Matrix4f projectionMatrix;
    
    protected RenderLevelEvent(final LevelRenderer levelRenderer,
                               final PoseStack poseStack,
                               final float partialTicks,
                               final Frustum frustum,
                               final Camera camera,
                               final long startTimeNano,
                               final Matrix4f projectionMatrix) {
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.partialTicks = partialTicks;
        this.frustum = frustum;
        this.camera = camera;
        this.startTimeNano = startTimeNano;
        this.projectionMatrix = projectionMatrix;
    }
    
    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }
    
    public PoseStack getPoseStack() {
        return poseStack;
    }
    
    public float getPartialTicks() {
        return partialTicks;
    }
    
    public Frustum getFrustum() {
        return frustum;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public long getStartTimeNano() {
        return startTimeNano;
    }
    
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
    
    /**
     * Fired immediately after glClear call
     */
    public static class BeginEvent extends RenderLevelEvent{
        public BeginEvent(final LevelRenderer levelRenderer,
                                      final PoseStack poseStack,
                                      final float partialTicks,
                                      final Frustum frustum,
                                      final Camera camera,
                                      final long startTimeNano,
                                      final Matrix4f projectionMatrix) {
            super(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix);
        }
    }
    
    /**
     * Fired after all rendering has completed, similar to RenderWorldLastEvent
     */
    public static class EndEvent extends RenderLevelEvent{
        public EndEvent(final LevelRenderer levelRenderer,
                          final PoseStack poseStack,
                          final float partialTicks,
                          final Frustum frustum,
                          final Camera camera,
                          final long startTimeNano,
                          final Matrix4f projectionMatrix) {
            super(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix);
        }
    }
    
    /**
     * Fired after sky rendering, can be used to check which parts of terrain needs updating.
     * <br>
     * If {@link System#nanoTime} is greater than <code>finishTimeNano</code> then you should quit this event immediately.
     */
    public static class TerrainUpdateEvent extends RenderLevelEvent {
        
        protected final long finishTimeNano;
        
        public TerrainUpdateEvent(final LevelRenderer levelRenderer,
                                  final PoseStack poseStack,
                                  final float partialTicks,
                                  final Frustum frustum,
                                  final Camera camera,
                                  final long startTimeNano,
                                  final Matrix4f projectionMatrix,
                                  final long finishTimeNano) {
            super(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix);
            this.finishTimeNano = finishTimeNano;
        }
        
        public long getFinishTimeNano() {
            return finishTimeNano;
        }
    }
    
    /**
     * Fired for each block layer being rendered. After vanilla block layer rendering, render type already set up.
     */
    public static class BlockLayerEvent extends RenderLevelEvent {
        protected final RenderType renderType;
        
        public BlockLayerEvent(final LevelRenderer levelRenderer,
                               final PoseStack poseStack,
                               final float partialTicks,
                               final Frustum frustum,
                               final Camera camera,
                               final long startTimeNano,
                               final Matrix4f projectionMatrix,
                               final RenderType renderType) {
            super(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix);
            this.renderType = renderType;
        }
        
        public RenderType getRenderType() {
            return renderType;
        }
    }
    
    /**
     * Fired right before finishing the buffer for given render type. Use the buffer builder.
     */
    public static class RenderTypeFinishEvent extends RenderLevelEvent {
        protected final RenderType renderType;
        protected final BufferBuilder bufferBuilder;
        
        public RenderTypeFinishEvent(final LevelRenderer levelRenderer,
                                     final PoseStack poseStack,
                                     final float partialTicks,
                                     final Frustum frustum,
                                     final Camera camera,
                                     final long startTimeNano,
                                     final Matrix4f projectionMatrix, final RenderType renderType,
                                     final BufferBuilder bufferBuilder) {
            super(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix);
            this.renderType = renderType;
            this.bufferBuilder = bufferBuilder;
        }
        
        public RenderType getRenderType() {
            return renderType;
        }
        
        public BufferBuilder getBufferBuilder() {
            return bufferBuilder;
        }
    }
    
    /**
     * Fired after the terrain and entities have been rendered
     */
    public static class TerrainAndEntitiesDoneEvent extends RenderLevelEvent {
        
        public TerrainAndEntitiesDoneEvent(final LevelRenderer levelRenderer,
                                           final PoseStack poseStack,
                                           final float partialTicks,
                                           final Frustum frustum,
                                           final Camera camera,
                                           final long startTimeNano,
                                           final Matrix4f projectionMatrix) {
            super(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix);
        }
    }
    
    
    /**
     * Fired after the weather has been rendered
     */
    public static class WeatherDoneEvent extends RenderLevelEvent {
        
        public WeatherDoneEvent(final LevelRenderer levelRenderer,
                                final PoseStack poseStack,
                                final float partialTicks,
                                final Frustum frustum,
                                final Camera camera,
                                final long startTimeNano,
                                final Matrix4f projectionMatrix) {
            super(levelRenderer, poseStack, partialTicks, frustum, camera, startTimeNano, projectionMatrix);
        }
    }
}