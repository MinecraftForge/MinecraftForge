/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.math.Matrix4f;

public class RenderLevelLastEvent extends net.minecraftforge.eventbus.api.Event
{
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final float partialTick;
    private final Matrix4f projectionMatrix;
    private final long startNanos;

    public RenderLevelLastEvent(LevelRenderer levelRenderer, PoseStack poseStack, float partialTick, Matrix4f projectionMatrix, long startNanos)
    {
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.partialTick = partialTick;
        this.projectionMatrix = projectionMatrix;
        this.startNanos = startNanos;
    }

    public LevelRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public float getPartialTick()
    {
        return partialTick;
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    public long getStartNanos()
    {
        return startNanos;
    }
}
