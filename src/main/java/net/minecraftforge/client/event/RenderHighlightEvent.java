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
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before a selection highlight is rendered.
 * See the two subclasses to listen for blocks or entities.
 *
 * @see Block
 * @see Entity
 */
public sealed interface RenderHighlightEvent {
    /**
     * {@return the level renderer}
     */
    LevelRenderer levelRenderer();

    /**
     * {@return the camera information}
     */
    Camera camera();

    /**
     * {@return the hit result which triggered the selection highlight}
     */
    HitResult target();

    /**
     * {@return the partial tick}
     */
    float partialTick();

    /**
     * {@return the pose stack used for rendering}
     */
    PoseStack poseStack();

    /**
     * {@return the source of rendering buffers}
     */
    MultiBufferSource multiBufferSource();

    /**
     * Fired before a block's selection highlight is rendered.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If the event is cancelled, then the selection highlight will not be rendered.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Block(
            LevelRenderer levelRenderer,
            Camera camera,
            BlockHitResult target,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource
    ) implements Cancellable, RecordEvent, RenderHighlightEvent {
        public static final CancellableEventBus<Block> BUS = CancellableEventBus.create(Block.class);

        /**
         * {@return the block hit result}
         */
        @Override
        public BlockHitResult target() {
            return target;
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
    record Entity(
            LevelRenderer levelRenderer,
            Camera camera,
            EntityHitResult target,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource
    ) implements RecordEvent, RenderHighlightEvent {
        public static final EventBus<Entity> BUS = EventBus.create(Entity.class);

        /**
         * {@return the entity hit result}
         */
        @Override
        public EntityHitResult target() {
            return target;
        }
    }
}
