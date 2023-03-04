/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.RegisterChunkBufferLayersEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;

import org.jetbrains.annotations.ApiStatus;

/**
 * Manager for ordered chunk {@link RenderType render types}.
 */
public final class ChunkBufferLayerManager
{
    private static ImmutableList<RenderType> RENDER_TYPES = ImmutableList.of();

    /**
     * Returns the ordered identifier of the given render type, or {@literal -1} if it is not a registered chunk buffer layer.
     *
     * @param value {@link RenderType} to search for.
     * @return the ordered identifier of the given chunk buffer layer, or {@literal -1} if it is not registered.
     */
    public static int getId(RenderType value)
    {
        return RENDER_TYPES.indexOf(value);
    }

    /**
     * Returns the list of all registered chunk buffer layers.
     *
     * @return the list of all registered chunk buffer layers.
     */
    public static List<RenderType> getRegisteredTypes()
    {
        return RENDER_TYPES;
    }

    @ApiStatus.Internal
    public static void init()
    {
        final var list = new LinkedList<RenderType>();
        preRegisterVanillaRenderTypes(list);
        final var event = new RegisterChunkBufferLayersEvent(list);
        ModLoader.get().postEventWithWrapInModOrder(event, (mc, e) -> ModLoadingContext.get().setActiveContainer(mc), (mc, e) -> ModLoadingContext.get().setActiveContainer(null));

        RENDER_TYPES = ImmutableList.copyOf(list);
    }

    private static void preRegisterVanillaRenderTypes(List<RenderType> chunkRenderTypes)
    {
        chunkRenderTypes.add(RenderType.solid());
        chunkRenderTypes.add(RenderType.cutoutMipped());
        chunkRenderTypes.add(RenderType.cutout());
    }

    private ChunkBufferLayerManager()
    {
    }
}
