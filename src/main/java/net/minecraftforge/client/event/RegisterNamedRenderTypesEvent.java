/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Allows users to register custom named {@link RenderType render types}.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 */
public class RegisterNamedRenderTypesEvent extends Event implements IModBusEvent
{
    private static final Set<RenderType> SUPPORTED_BLOCK_RENDER_TYPES = Util.make(Collections.newSetFromMap(new IdentityHashMap<>()), set -> {
        set.addAll(RenderType.chunkBufferLayers());
    });

    private final Map<ResourceLocation, RenderTypeGroup> renderTypes;

    @ApiStatus.Internal
    public RegisterNamedRenderTypesEvent(Map<ResourceLocation, RenderTypeGroup> renderTypes)
    {
        this.renderTypes = renderTypes;
    }

    /**
     * Registers a named render type.<p/>
     * {@code blockRenderType} must use {@link DefaultVertexFormat#BLOCK} and
     * {@code entityRenderType} must use {@link DefaultVertexFormat#NEW_ENTITY}.<p/>
     * Note: The only render types currently supported for block rendering are the ones returned by
     * {@link RenderType#chunkBufferLayers()}. Any other types will cause an exception.
     */
    public void register(String name, RenderType blockRenderType, RenderType entityRenderType)
    {
        register(name, blockRenderType, entityRenderType, entityRenderType);
    }

    /**
     * Registers a named render type.<p/>
     * {@code blockRenderType} must use {@link DefaultVertexFormat#BLOCK} and {@code entityRenderType} and
     * {@code fabulousEntityRenderType} must use {@link DefaultVertexFormat#NEW_ENTITY}.<p/>
     * Note: The only render types currently supported for block rendering are the ones returned by
     * {@link RenderType#chunkBufferLayers()}. Any other types will cause an exception.
     */
    public void register(String name, RenderType blockRenderType, RenderType entityRenderType, RenderType fabulousEntityRenderType)
    {
        var key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
        Preconditions.checkArgument(!renderTypes.containsKey(key), "Render type already registered: " + key);
        Preconditions.checkArgument(blockRenderType.format() == DefaultVertexFormat.BLOCK, "The block render type must use the BLOCK vertex format.");
        Preconditions.checkArgument(SUPPORTED_BLOCK_RENDER_TYPES.contains(blockRenderType), "Only Minecraft's built-in block render types are supported at this time.");
        Preconditions.checkArgument(entityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The entity render type must use the NEW_ENTITY vertex format.");
        Preconditions.checkArgument(fabulousEntityRenderType.format() == DefaultVertexFormat.NEW_ENTITY, "The fabulous entity render type must use the NEW_ENTITY vertex format.");
        renderTypes.put(key, new RenderTypeGroup(blockRenderType, entityRenderType, fabulousEntityRenderType));
    }
}
