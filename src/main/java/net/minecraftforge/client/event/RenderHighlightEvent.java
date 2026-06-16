/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Fired before a selection highlight is rendered.<br>
 * You should extract your custom data here so that you can use it in the render callback without effecting world state.
 * <br><br>
 * See the two subclasses to listen for blocks or entities.
 *
 * @see Block
 * @see Entity
 */
public sealed abstract class RenderHighlightEvent extends MutableEvent implements Cancellable, InheritableEvent permits RenderHighlightEvent.Block, RenderHighlightEvent.Entity {
    CancellableEventBus<RenderHighlightEvent> BUS = CancellableEventBus.create(RenderHighlightEvent.class);

    private final LevelExtractor levelExtractor;
    private final Camera camera;
    private final LevelRenderState levelRenderState;
    private Callback customRenderer;

    private RenderHighlightEvent(LevelExtractor levelExtractor, Camera camera, LevelRenderState levelRenderState) {
        this.levelExtractor = levelExtractor;
        this.camera = camera;
        this.levelRenderState = levelRenderState;
    }

    /**
     * {@return the level renderer}
     */
    public LevelExtractor getLevelExtractor() {
        return this.levelExtractor;
    }

    /**
     * {@return the level renderer state}
     */
    public LevelRenderState getLevelRenderState() {
        return this.levelRenderState;
    }

    /**
     * {@return the camera information}
     */
    public Camera getCamera() {
        return this.camera;
    }

    /**
     * {@return the hit result which triggered the selection highlight}
     */
    abstract HitResult getTarget();

    /**
     * {@return the custom renderer that an event listener has set}
     */
    @Nullable
    public Callback getCustomRenderer() {
        return this.customRenderer;
    }

    /**
     * Set a custom renderer to render the selection highlight.
     * May be null to clear a previously set custom renderer.
     *
     * If the callback is set, it will be invoked instead of the vanilla rendering.
     */
    public void setCustomRenderer(@Nullable Callback value) {
        this.customRenderer = value;
    }

    /**
     * Fired before a block's selection highlight is rendered.
     *
     * <p>This event is {@linkplain Cancellable cancellable}.
     * If the event is cancelled, then the selection highlight will not be rendered.</p>
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static final class Block extends RenderHighlightEvent {
        public static final CancellableEventBus<Block> BUS = CancellableEventBus.create(Block.class);
        private final BlockHitResult target;

        @ApiStatus.Internal
        public Block(LevelExtractor levelExtrctor, Camera camera, LevelRenderState levelRenderState, BlockHitResult target) {
            super(levelExtrctor, camera, levelRenderState);
            this.target = target;
        }

        /**
         * {@return the block hit result}
         */
        @Override
        public BlockHitResult getTarget() {
            return target;
        }
    }

    /**
     * Fired before an entity's selection highlight is rendered.
     *
     * <p>This event is not {@linkplain Cancellable cancellable}.</p>
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static final class Entity extends RenderHighlightEvent {
        public static final CancellableEventBus<Entity> BUS = CancellableEventBus.create(Entity.class);
        private final EntityHitResult target;

        @ApiStatus.Internal
        public Entity(LevelExtractor levelExtractor, Camera camera, LevelRenderState levelRenderState, EntityHitResult target) {
            super(levelExtractor, camera, levelRenderState);
            this.target = target;
        }

        /**
         * {@return the entity hit result}
         */
        @Override
        public EntityHitResult getTarget() {
            return target;
        }
    }

    public interface Callback {
        void render(SubmitNodeCollector submitNodeCollector, PoseStack stack, LevelRenderState state);
    }
}
