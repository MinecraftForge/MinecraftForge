/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraftsingularity.common.util.Result;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Fired before an entity renderer renders the nameplate of an entity.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * Setting any of the content to null will prevent that content from being rendered.
 * If you wish to cancel both score and name tag, you can cancel the entire event.
 *
 * @see EntityRenderer
 */
public final class RenderNameTagEvent extends MutableEvent implements Cancellable {
    public static final EventBus<RenderNameTagEvent> BUS = CancellableEventBus.create(RenderNameTagEvent.class);

    private Component nameplateContent;
    private Component scoreContent;
    private final EntityRenderState state;
    private final Component originalContent;
    private final Component originalScoreContent;
    private final EntityRenderer<?, ?> entityRenderer;
    private final PoseStack poseStack;
    private final SubmitNodeCollector nodeCollector;
    private final CameraRenderState cameraState;

    @ApiStatus.Internal
    public RenderNameTagEvent(EntityRenderState state, EntityRenderer<?, ?> entityRenderer, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraState) {
        this.state = state;
        this.originalContent = this.nameplateContent = state.nameTag;
        this.originalScoreContent = this.scoreContent = state.scoreText;
        this.setContent(this.originalContent);
        this.entityRenderer = entityRenderer;
        this.poseStack = poseStack;
        this.nodeCollector = nodeCollector;
        this.cameraState = cameraState;
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityRenderState> T getState() {
        return (T)state;
    }

    /**
     * Sets the new text on the nameplate.
     *
     * @param contents the new text
     */
    public void setContent(@Nullable Component contents) {
        this.nameplateContent = contents;
    }

    /**
     * {@return the text on the nameplate that will be rendered, if the event is not {@link Result#DENY DENIED}}
     */
    public @Nullable Component getContent() {
        return this.nameplateContent;
    }

    /**
     * Sets the new text for the score part of the nameplate.
     *
     * @param contents the new text
     */
    public void setScoreContent(@Nullable Component contents) {
        this.scoreContent = contents;
    }

    /**
     * {@return the text on the score part of the nameplate that will be rendered, if the event is not {@link Result#DENY DENIED}}
     */
    public @Nullable Component getScoreContent() {
        return this.scoreContent;
    }

    /**
     * {@return the original text on the nameplate}
     */
    public @Nullable Component getOriginalContent() {
        return this.originalContent;
    }

    /**
     * {@return the original text of the score part of this nameplate}
     */
    public @Nullable Component getOriginalScore() {
        return this.originalScoreContent;
    }


    /**
     * {@return the entity renderer rendering the nameplate}
     */
    public EntityRenderer<?, ?> getEntityRenderer() {
        return this.entityRenderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    /**
     * {@return the node collector that you should render to}
     */
    public SubmitNodeCollector getNodeCollector() {
        return this.nodeCollector;
    }

    /**
     * {@return CameraState for the current render frame}
     */
    public CameraRenderState getCameraState() {
        return this.cameraState;
    }
}
