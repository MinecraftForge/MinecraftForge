/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.math.Matrix4f;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired after all level rendering.
 * This can be used for custom rendering outside of e.g. a block entity or entity renderer.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see GameRenderer
 * @see LevelRenderer
 */
public class RenderLevelLastEvent extends net.minecraftforge.eventbus.api.Event
{
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final float partialTick;
    private final Matrix4f projectionMatrix;
    private final long startNanos;

    /**
     * @hidden
     * @see ForgeHooksClient#dispatchRenderLast(LevelRenderer, PoseStack, float, Matrix4f, long)
     */
    public RenderLevelLastEvent(LevelRenderer levelRenderer, PoseStack poseStack, float partialTick, Matrix4f projectionMatrix, long startNanos)
    {
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.partialTick = partialTick;
        this.projectionMatrix = projectionMatrix;
        this.startNanos = startNanos;
    }

    /**
     * {@return the level renderer}
     */
    public LevelRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the projection matrix}
     */
    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    /**
     * {@return the time when rendering started, in nanoseconds}
     */
    public long getStartNanos()
    {
        return startNanos;
    }
}
