/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
 * 
 * TODO: Rename to DrawSelectionEvent
 */
@Cancelable
public class DrawHighlightEvent extends Event
{
    private final WorldRenderer context;
    private final ActiveRenderInfo info;
    private final RayTraceResult target;
    private final float partialTicks;
    private final MatrixStack matrix;
    private final IRenderTypeBuffer buffers;

    public DrawHighlightEvent(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffers)
    {
        this.context = context;
        this.info = info;
        this.target = target;
        this.partialTicks= partialTicks;
        this.matrix = matrix;
        this.buffers = buffers;
    }

    public WorldRenderer getContext() { return context; }
    public ActiveRenderInfo getInfo() { return info; }
    public RayTraceResult getTarget() { return target; }
    public float getPartialTicks() { return partialTicks; }
    public MatrixStack getMatrix() { return matrix; }
    public IRenderTypeBuffer getBuffers() { return buffers; }

    /**
     * A variant of the DrawHighlightEvent only called when a block is highlighted.
     */
    @Cancelable
    public static class HighlightBlock extends DrawHighlightEvent
    {
        public HighlightBlock(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffers)
        {
            super(context, info, target, partialTicks, matrix, buffers);
        }

        @Override
        public BlockRayTraceResult getTarget()
        {
            return (BlockRayTraceResult) super.target;
        }
    }

    /**
     * A variant of the DrawHighlightEvent only called when an entity is highlighted.
     * Canceling this event has no effect.
     */
    @Cancelable
    public static class HighlightEntity extends DrawHighlightEvent
    {
        public HighlightEntity(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffers)
        {
            super(context, info, target, partialTicks, matrix, buffers);
        }

        @Override
        public EntityRayTraceResult getTarget()
        {
            return (EntityRayTraceResult) super.target;
        }
    }
}
