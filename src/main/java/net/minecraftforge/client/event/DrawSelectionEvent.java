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
    private final LevelRenderer context;
    private final Camera info;
    private final HitResult target;
    private final float partialTicks;
    private final PoseStack matrix;
    private final MultiBufferSource buffers;

    public DrawSelectionEvent(LevelRenderer context, Camera info, HitResult target, float partialTicks, PoseStack matrix, MultiBufferSource buffers)
    {
        this.context = context;
        this.info = info;
        this.target = target;
        this.partialTicks= partialTicks;
        this.matrix = matrix;
        this.buffers = buffers;
    }

    public LevelRenderer getContext() { return context; }
    public Camera getInfo() { return info; }
    public HitResult getTarget() { return target; }
    public float getPartialTicks() { return partialTicks; }
    public PoseStack getMatrix() { return matrix; }
    public MultiBufferSource getBuffers() { return buffers; }

    /**
     * A variant of the DrawSelectionEvent only called when a block is highlighted.
     */
    @Cancelable
    public static class HighlightBlock extends DrawSelectionEvent
    {
        public HighlightBlock(LevelRenderer context, Camera info, HitResult target, float partialTicks, PoseStack matrix, MultiBufferSource buffers)
        {
            super(context, info, target, partialTicks, matrix, buffers);
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
        public HighlightEntity(LevelRenderer context, Camera info, HitResult target, float partialTicks, PoseStack matrix, MultiBufferSource buffers)
        {
            super(context, info, target, partialTicks, matrix, buffers);
        }

        @Override
        public EntityHitResult getTarget()
        {
            return (EntityHitResult) super.target;
        }
    }
}
