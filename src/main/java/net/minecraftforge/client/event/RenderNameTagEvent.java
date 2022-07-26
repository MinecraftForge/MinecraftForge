/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before an entity renderer renders the nameplate of an entity.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and  {@linkplain HasResult has a result}.</p>
 * <ul>
 *     <li>{@link Result#ALLOW} - the nameplate will be forcibly rendered.</li>
 *     <li>{@link Result#DEFAULT} - the vanilla logic will be used.</li>
 *     <li>{@link Result#DENY} - the nameplate will not be rendered.</li>
 * </ul>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see EntityRenderer
 */
@Event.HasResult
public class RenderNameTagEvent extends EntityEvent
{
    private Component nameplateContent;
    private final Component originalContent;
    private final EntityRenderer<?> entityRenderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final float partialTick;

    @ApiStatus.Internal
    public RenderNameTagEvent(Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick)
    {
        super(entity);
        this.originalContent = content;
        this.setContent(this.originalContent);
        this.entityRenderer = entityRenderer;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.partialTick = partialTick;
    }

    /**
     * Sets the new text on the nameplate.
     *
     * @param contents the new text
     */
    public void setContent(Component contents)
    {
        this.nameplateContent = contents;
    }

    /**
     * {@return the text on the nameplate that will be rendered, if the event is not {@link Result#DENY DENIED}}
     */
    public Component getContent()
    {
        return this.nameplateContent;
    }

    /**
     * {@return the original text on the nameplate}
     */
    public Component getOriginalContent()
    {
        return this.originalContent;
    }

    /**
     * {@return the entity renderer rendering the nameplate}
     */
    public EntityRenderer<?> getEntityRenderer()
    {
        return this.entityRenderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return this.poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getMultiBufferSource()
    {
        return this.multiBufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see net.minecraft.client.renderer.LightTexture
     */
    public int getPackedLight()
    {
        return this.packedLight;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return this.partialTick;
    }
}
