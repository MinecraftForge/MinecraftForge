/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired after all level rendering.
 * This can be used for custom rendering outside of e.g. a block entity or entity renderer.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @deprecated Use {@link RenderLevelStageEvent} instead for more flexibility and improved compatibility with translucent objects.
 * There is no {@link RenderLevelStageEvent.Stage} that directly replaces this event, instead you must decide which Stage best fits your use case.
 * @see GameRenderer
 * @see LevelRenderer
 */
@Deprecated(forRemoval = true, since = "1.19")
public class RenderLevelLastEvent extends net.minecraftforge.eventbus.api.Event
{
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final float partialTick;
    private final Matrix4f projectionMatrix;
    private final long startNanos;

    @ApiStatus.Internal
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
