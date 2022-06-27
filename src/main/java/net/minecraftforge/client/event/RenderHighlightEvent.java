/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before a selection highlight is rendered.
 * See the two subclasses to listen for blocks or entities.
 *
 * @see Block
 * @see Entity
 */
@Cancelable
public abstract class RenderHighlightEvent extends Event
{
    private final LevelRenderer levelRenderer;
    private final Camera camera;
    private final HitResult target;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;

    @ApiStatus.Internal
    protected RenderHighlightEvent(LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource)
    {
        this.levelRenderer = levelRenderer;
        this.camera = camera;
        this.target = target;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
    }

    /**
     * {@return the level renderer}
     */
    public LevelRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    /**
     * {@return the camera information}
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * {@return the hit result which triggered the selection highlight}
     */
    public HitResult getTarget()
    {
        return target;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getMultiBufferSource()
    {
        return multiBufferSource;
    }

    /**
     * Fired before a block's selection highlight is rendered.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If the event is cancelled, then the selection highlight will not be rendered.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class Block extends RenderHighlightEvent
    {
        @ApiStatus.Internal
        public Block(LevelRenderer levelRenderer, Camera camera, BlockHitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, target, partialTick, poseStack, bufferSource);
        }

        /**
         * {@return the block hit result}
         */
        @Override
        public BlockHitResult getTarget()
        {
            return (BlockHitResult) super.target;
        }
    }

    /**
     * Fired before an entity's selection highlight is rendered.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Entity extends RenderHighlightEvent
    {
        @ApiStatus.Internal
        public Entity(LevelRenderer levelRenderer, Camera camera, EntityHitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, target, partialTick, poseStack, bufferSource);
        }

        /**
         * {@return the entity hit result}
         */
        @Override
        public EntityHitResult getTarget()
        {
            return (EntityHitResult) super.target;
        }
    }
}
