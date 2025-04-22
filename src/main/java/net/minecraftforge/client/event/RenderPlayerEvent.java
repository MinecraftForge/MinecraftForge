/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a player is being rendered.
 * See the two subclasses for listening for before and after rendering.
 *
 * @see RenderPlayerEvent.Pre
 * @see RenderPlayerEvent.Post
 * @see PlayerRenderer
 */
public abstract sealed class RenderPlayerEvent extends Event {
    private final PlayerRenderState state;
    private final PlayerRenderer renderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    @ApiStatus.Internal
    protected RenderPlayerEvent(PlayerRenderState state, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.state = state;
        this.renderer = renderer;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    public PlayerRenderState getState() {
        return this.state;
    }

    /**
     * {@return the player entity renderer}
     */
    public PlayerRenderer getRenderer() {
        return renderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack() {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight() {
        return packedLight;
    }

    /**
     * Fired <b>before</b> the player is rendered.
     * This can be used for rendering additional effects or suppressing rendering.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the player will not be rendered and the corresponding
     * {@link RenderPlayerEvent.Post} will not be fired.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static final class Pre extends RenderPlayerEvent {
        @ApiStatus.Internal
        public Pre(PlayerRenderState state, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(state, renderer, poseStack, multiBufferSource, packedLight);
        }
    }

    /**
     * Fired <b>after</b> the player is rendered, if the corresponding {@link RenderPlayerEvent.Pre} is not cancelled.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static final class Post extends RenderPlayerEvent {
        @ApiStatus.Internal
        public Post(PlayerRenderState state, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(state, renderer, poseStack, multiBufferSource, packedLight);
        }
    }
}
