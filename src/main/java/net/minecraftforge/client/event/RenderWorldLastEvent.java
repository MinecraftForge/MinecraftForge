/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.math.Matrix4f;

public class RenderWorldLastEvent extends net.minecraftforge.eventbus.api.Event
{
    private final LevelRenderer context;
    private final PoseStack mat;
    private final float partialTicks;
    private final Matrix4f projectionMatrix;
    private final long finishTimeNano;

    public RenderWorldLastEvent(LevelRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix, long finishTimeNano)
    {
        this.context = context;
        this.mat = mat;
        this.partialTicks = partialTicks;
        this.projectionMatrix = projectionMatrix;
        this.finishTimeNano = finishTimeNano;
    }

    public LevelRenderer getContext()
    {
        return context;
    }

    public PoseStack getMatrixStack()
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
