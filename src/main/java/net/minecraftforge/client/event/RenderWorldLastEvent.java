/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.WorldRenderer;

public class RenderWorldLastEvent extends net.minecraftforge.eventbus.api.Event
{
    private final WorldRenderer context;
    private final MatrixStack mat;
    private final float partialTicks;
    private final Matrix4f projectionMatrix;
    private final long finishTimeNano;

    @Deprecated // TODO: Remove in 1.16
    public RenderWorldLastEvent(WorldRenderer context, MatrixStack mat, float partialTicks)
    {
        this.context = context;
        this.mat = mat;
        this.partialTicks = partialTicks;
        this.projectionMatrix = null;
        this.finishTimeNano = 0;
    }

    public RenderWorldLastEvent(WorldRenderer context, MatrixStack mat, float partialTicks, Matrix4f projectionMatrix, long finishTimeNano)
    {
        this.context = context;
        this.mat = mat;
        this.partialTicks = partialTicks;
        this.projectionMatrix = projectionMatrix;
        this.finishTimeNano = finishTimeNano;
    }

    public WorldRenderer getContext()
    {
        return context;
    }

    public MatrixStack getMatrixStack()
    {
        return mat;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    public long getFinishTimeNano()
    {
        return finishTimeNano;
    }
}
