/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a player is being rendered.
 * See the two subclasses for listening for before and after rendering.
 *
 * @see RenderPlayerEvent.AboutToRender
 * @see RenderPlayerEvent.Pre
 * @see RenderPlayerEvent.Post
 * @see PlayerRenderer
 */
public abstract class RenderPlayerEvent extends PlayerEvent
{
    private final PlayerRenderer renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    @ApiStatus.Internal
    protected RenderPlayerEvent(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
    {
        super(player);
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    /**
     * {@return the player entity renderer}
     */
    public PlayerRenderer getRenderer()
    {
        return renderer;
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
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight()
    {
        return packedLight;
    }

    /**
     * Fired <b>before</b> the player is rendered.
     * This should be used for suppressing rendering.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the player will not be rendered.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class AboutToRender extends RenderPlayerEvent
    {
        @ApiStatus.Internal
        public AboutToRender(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }

    /**
     * Fired <b>before</b> the player is rendered. This can be used for rendering additional effects.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Pre extends RenderPlayerEvent
    {
        @ApiStatus.Internal
        public Pre(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }

        /**
         * The functionality to cancel the player render has been moved to {@link AboutToRender}
         * @param cancel The new canceled value
         */
        @Deprecated(since = "1.20.1", forRemoval = true)
        @Override
        public void setCanceled(boolean cancel) {
            super.setCanceled(cancel);
        }
    }

    /**
     * Fired <b>after</b> the player is rendered. This event can be used to render additional effects. If you have
     * pushed to the pose stack in your pre event don't forget to pop here if you have not done so already.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Post extends RenderPlayerEvent
    {
        @ApiStatus.Internal
        public Post(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }
}
