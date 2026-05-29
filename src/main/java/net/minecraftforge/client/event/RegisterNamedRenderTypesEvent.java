/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.RenderTypeGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Allows users to register custom named {@link RenderType render types}.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public final class RegisterNamedRenderTypesEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterNamedRenderTypesEvent> BUS = EventBus.create(RegisterNamedRenderTypesEvent.class);

    private final Map<Identifier, RenderTypeGroup> renderTypes;

    @ApiStatus.Internal
    public RegisterNamedRenderTypesEvent(Map<Identifier, RenderTypeGroup> renderTypes) {
        this.renderTypes = renderTypes;
    }

    /**
     * Registers a named {@link RenderTypeGroup}.
     *
     * @param Identifier The namespace should match your mod's namespace, such as your mod ID
     * @param blockRenderType  What ChunkSectionLayer to render in
     * @param entityRenderType A {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY}
     */
    public void register(Identifier Identifier, ChunkSectionLayer blockRenderType, RenderType entityRenderType) {
        register(Identifier, blockRenderType, entityRenderType, entityRenderType);
    }

    /**
     * Registers a named {@link RenderTypeGroup}.
     *
     * @param key                      The namespace should match your mod's namespace, such as your mod ID
     * @param blockRenderType          What ChunkSectionLayer to render in
     * @param entityRenderType         A {@link RenderType} using {@link DefaultVertexFormat#ENTITY}
     * @param fabulousEntityRenderType A {@link RenderType} using {@link DefaultVertexFormat#ENTITY} for use when
     *                                 "fabulous" rendering is enabled
     */
    public void register(Identifier key, ChunkSectionLayer blockRenderType, RenderType entityRenderType, RenderType fabulousEntityRenderType) {
        Preconditions.checkArgument(!renderTypes.containsKey(key), "Render type already registered: " + key);
        Preconditions.checkArgument(entityRenderType.format() == DefaultVertexFormat.ENTITY, "The entity render type must use the ENTITY vertex format.");
        Preconditions.checkArgument(fabulousEntityRenderType.format() == DefaultVertexFormat.ENTITY, "The fabulous entity render type must use the ENTITY vertex format.");
        renderTypes.put(key, new RenderTypeGroup(blockRenderType, entityRenderType, fabulousEntityRenderType));
    }
}
