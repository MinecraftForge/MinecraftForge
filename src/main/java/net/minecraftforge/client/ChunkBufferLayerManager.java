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
    private static ImmutableList<RenderType> SOLID_RENDER_TYPES = ImmutableList.of();
    private static ImmutableList<RenderType> TRANSLUCENT_RENDER_TYPES = ImmutableList.of();
    private static ImmutableList<RenderType> TRIPWIRE_RENDER_TYPES = ImmutableList.of();
    private static ImmutableList<RenderType> ALL_RENDER_TYPES = ImmutableList.of();

    /**
     * Returns the ordered identifier of the given render type, or {@literal -1} if it is not a registered chunk buffer layer.
     *
     * @param value {@link RenderType} to search for.
     * @return the ordered identifier of the given chunk buffer layer, or {@literal -1} if it is not registered.
     */
    public static int getId(RenderType value)
    {
        return ALL_RENDER_TYPES.indexOf(value);
    }

    /**
     * Returns the list of all registered chunk buffer layers.
     *
     * @return the list of all registered chunk buffer layers.
     */
    public static List<RenderType> getAllRegisteredRenderTypes()
    {
        return ALL_RENDER_TYPES;
    }

    /**
     * Returns the list of all registered solid chunk buffer layers.
     *
     * @return the list of all registered solid chunk buffer layers.
     */
    public static List<RenderType> getRegisteredSolidRenderTypes()
    {
        return SOLID_RENDER_TYPES;
    }

    /**
     * Returns the list of all registered translucent chunk buffer layers.
     *
     * @return the list of all registered translucent chunk buffer layers.
     */
    public static List<RenderType> getRegisteredTranslucentRenderTypes()
    {
        return TRANSLUCENT_RENDER_TYPES;
    }

    /**
     * Returns the list of all registered tripwire chunk buffer layers.
     *
     * @return the list of all registered tripwire chunk buffer layers.
     */
    public static List<RenderType> getRegisteredTripwireRenderTypes()
    {
        return TRIPWIRE_RENDER_TYPES;
    }

    @ApiStatus.Internal
    public static void init()
    {
        final var solid = new LinkedList<RenderType>();
        final var translucent = new LinkedList<RenderType>();
        final var tripwire = new LinkedList<RenderType>();

        preRegisterVanillaSolidRenderTypes(solid);
        preRegisterVanillaTranslucentRenderTypes(translucent);
        preRegisterVanillaTripwireRenderTypes(tripwire);

        final var event = new RegisterChunkBufferLayersEvent(solid, translucent, tripwire);
        ModLoader.get().postEventWithWrapInModOrder(event, (mc, e) -> ModLoadingContext.get().setActiveContainer(mc), (mc, e) -> ModLoadingContext.get().setActiveContainer(null));

        final var all = ImmutableList.<RenderType>builderWithExpectedSize(solid.size() + translucent.size() + tripwire.size());
        all.addAll(solid);
        all.addAll(translucent);
        all.addAll(tripwire);

        SOLID_RENDER_TYPES = ImmutableList.copyOf(solid);
        TRANSLUCENT_RENDER_TYPES = ImmutableList.copyOf(translucent);
        TRIPWIRE_RENDER_TYPES = ImmutableList.copyOf(tripwire);
        ALL_RENDER_TYPES = all.build();
    }

    private static void preRegisterVanillaSolidRenderTypes(List<RenderType> chunkRenderTypes)
    {
        chunkRenderTypes.add(RenderType.solid());
        chunkRenderTypes.add(RenderType.cutoutMipped());
        chunkRenderTypes.add(RenderType.cutout());
    }

    private static void preRegisterVanillaTranslucentRenderTypes(List<RenderType> chunkRenderTypes)
    {
        chunkRenderTypes.add(RenderType.translucent());
    }

    private static void preRegisterVanillaTripwireRenderTypes(List<RenderType> chunkRenderTypes)
    {
        chunkRenderTypes.add(RenderType.tripwire());
    }

    private ChunkBufferLayerManager()
    {
    }
}
