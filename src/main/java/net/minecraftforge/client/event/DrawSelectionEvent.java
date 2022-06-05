/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;

/**
 * An event called whenever the selection highlight around a block or entity is about to be rendered.
 * Canceling this event stops the selection highlight from being rendered.
 */
@Cancelable
public class DrawSelectionEvent extends Event
{
    private final LevelRenderer levelRenderer;
    private final Camera camera;
    private final HitResult target;
    private final float partialTicks;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;

    public DrawSelectionEvent(LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource)
    {
        this.levelRenderer = levelRenderer;
        this.camera = camera;
        this.target = target;
        this.partialTicks = partialTicks;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
    }

    public LevelRenderer getLevelRenderer() { return levelRenderer; }
    public Camera getCamera() { return camera; }
    public HitResult getTarget() { return target; }
    public float getPartialTicks() { return partialTicks; }
    public PoseStack getPoseStack() { return poseStack; }
    public MultiBufferSource getMultiBufferSource() { return multiBufferSource; }

    /**
     * A variant of the DrawSelectionEvent only called when a block is highlighted.
     */
    @Cancelable
    public static class HighlightBlock extends DrawSelectionEvent
    {
        public HighlightBlock(LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, target, partialTick, poseStack, bufferSource);
        }

        @Override
        public BlockHitResult getTarget()
        {
            return (BlockHitResult) super.target;
        }
    }

    /**
     * A variant of the DrawSelectionEvent only called when an entity is highlighted.
     * Canceling this event has no effect.
     */
    @Cancelable
    public static class HighlightEntity extends DrawSelectionEvent
    {
        public HighlightEntity(LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, target, partialTick, poseStack, bufferSource);
        }

        @Override
        public EntityHitResult getTarget()
        {
            return (EntityHitResult) super.target;
        }
    }
}
