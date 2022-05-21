/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

/**
 * RenderNameplateEvent is fired whenever the entity renderer attempts to render a name plate/tag of an entity.
 * <br>
 * {@link #nameplateContent} contains the content being rendered on the name plate/tag. This can be changed by mods.<br>
 * {@link #originalContent} contains the original content being rendered on the name plate/tag. This cannot be
 * changed by mods.<br>
 * {@link #entityRenderer} contains the entity renderer instance that renders the name plate/tag. This cannot be
 * changed by mods.<br>
 * {@link #poseStack} contains the matrix stack instance involved in rendering the name plate/tag. This cannot
 * be changed by mods.<br>
 * {@link #multiBufferSource} contains the render type buffer instance involved in rendering the name plate/tag.
 * This cannot be changed by mods.<br>
 * {@link #packedLight} contains the sky and block light values used in rendering the name plate/tag.<br>
 * {@link #partialTick} contains the partial ticks used in rendering the name plate/tag. This cannot be changed by mods.<br>
 * <br>
 * This event has a result. {@link HasResult}. <br>
 * ALLOW will force-render name plate/tag, DEFAULT will ignore the hook and continue using the vanilla check
 * and DENY will prevent name plate/tag from rendering<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Event.HasResult
public class RenderNameplateEvent extends EntityEvent
{

    private Component nameplateContent;
    private final Component originalContent;
    private final EntityRenderer<?> entityRenderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final float partialTick;
    
    public RenderNameplateEvent(Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick)
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
     * Sets the content that is to be rendered on the name plate/tag
     */
    public void setContent(Component contents)
    {
        this.nameplateContent = contents;
    }

    /**
     * The content being rendered on the name plate/tag
     */
    public Component getContent()
    {
        return this.nameplateContent;
    }

    /**
     * The original content being rendered on the name plate/tag
     */
    public Component getOriginalContent()
    {
        return this.originalContent;
    }

    /**
     * The entity renderer that renders the name plate/tag, if it was provided
     */
    public EntityRenderer<?> getEntityRenderer()
    {
        return this.entityRenderer;
    }

    /**
     * The matrix stack used during the rendering of the name plate/tag
     */
    public PoseStack getPoseStack()
    {
        return this.poseStack;
    }

    /**
     * The render type buffer used during the rendering of the name plate/tag
     */
    public MultiBufferSource getMultiBufferSource()
    {
        return this.multiBufferSource;
    }

    /**
     * The packed values of sky and block light used during the rendering of the name plate/tag
     */
    public int getPackedLight()
    {
        return this.packedLight;
    }
    
    /**
     * The partial ticks used during the rendering of the name plate/tag
     */
    public float getPartialTick()
    {
        return this.partialTick;
    }
}
