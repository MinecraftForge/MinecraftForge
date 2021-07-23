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

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event called whenever the selection highlight around a block or entity is about to be rendered.
 * Canceling this event stops the selection highlight from being rendered.
 */
@Cancelable
public class DrawSelectionEvent extends Event
{
    private final LevelRenderer levelRenderer;
    private final Camera camera;
    private final HitResult hitResult;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;

    public DrawSelectionEvent(LevelRenderer levelRenderer, Camera camera, HitResult hitResult, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
    {
        this.levelRenderer = levelRenderer;
        this.camera = camera;
        this.hitResult = hitResult;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
    }

    public LevelRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public HitResult getHitResult()
    {
        return hitResult;
    }

    public float getPartialTick()
    {
        return partialTick;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    /**
     * A variant of the DrawSelectionEvent only called when a block is highlighted.
     */
    @Cancelable
    public static class HighlightBlock extends DrawSelectionEvent
    {
        public HighlightBlock(LevelRenderer levelRenderer, Camera camera, BlockHitResult hitResult, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, hitResult, partialTick, poseStack, bufferSource);
        }

        @Override
        public BlockHitResult getHitResult()
        {
            return (BlockHitResult) super.hitResult;
        }
    }

    /**
     * A variant of the DrawSelectionEvent only called when an entity is highlighted.
     * Canceling this event has no effect.
     */
    @Cancelable
    public static class HighlightEntity extends DrawSelectionEvent
    {
        public HighlightEntity(LevelRenderer levelRenderer, Camera camera, EntityHitResult hitResult, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, hitResult, partialTick, poseStack, bufferSource);
        }

        @Override
        public EntityHitResult getHitResult()
        {
            return (EntityHitResult) super.hitResult;
        }
    }
}
