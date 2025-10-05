/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Allows users to register custom named {@link RenderType render types}.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public final class RegisterNamedRenderTypesEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterNamedRenderTypesEvent> BUS = EventBus.create(RegisterNamedRenderTypesEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterNamedRenderTypesEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final Map<ResourceLocation, RenderTypeGroup> renderTypes;

    @ApiStatus.Internal
    public RegisterNamedRenderTypesEvent(Map<ResourceLocation, RenderTypeGroup> renderTypes) {
        this.renderTypes = renderTypes;
    }

    /**
     * Registers a named {@link RenderTypeGroup}.
     *
     * @param resourceLocation The namespace should match your mod's namespace, such as your mod ID
     * @param blockRenderType  What ChunkSectionLayer to render in
     * @param entityRenderType A {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY}
     */
    public void register(ResourceLocation resourceLocation, ChunkSectionLayer blockRenderType, RenderType entityRenderType) {
        register(resourceLocation, blockRenderType, entityRenderType, entityRenderType);
    }

    /**
     * Registers a named {@link RenderTypeGroup}.
     *
     * @param key                      The namespace should match your mod's namespace, such as your mod ID
     * @param blockRenderType          What ChunkSectionLayer to render in
     * @param entityRenderType         A {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY}
     * @param fabulousEntityRenderType A {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY} for use when
     *                                 "fabulous" rendering is enabled
     */
    public void register(ResourceLocation key, ChunkSectionLayer blockRenderType, RenderType entityRenderType, RenderType fabulousEntityRenderType) {
        Preconditions.checkArgument(!renderTypes.containsKey(key), "Render type already registered: " + key);
        Preconditions.checkArgument(entityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The entity render type must use the NEW_ENTITY vertex format.");
        Preconditions.checkArgument(fabulousEntityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The fabulous entity render type must use the NEW_ENTITY vertex format.");
        renderTypes.put(key, new RenderTypeGroup(blockRenderType, entityRenderType, fabulousEntityRenderType));
    }
}
