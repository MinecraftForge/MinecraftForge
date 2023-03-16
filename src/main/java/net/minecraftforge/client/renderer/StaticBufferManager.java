/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.RegisterRenderTypesEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;

final class StaticBufferManager
{
    private final Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> registeredBuffers;
    private final ImmutableList<RenderType> renderTypes;

    public StaticBufferManager(List<RenderType> initial,
        Function<List<RenderType>, RegisterRenderTypesEvent> phase)
    {
        registeredBuffers = new Object2ObjectLinkedOpenHashMap<>();

        var builder = new LinkedList<RenderType>(initial);
        ModLoader.get().postEventWithWrapInModOrder(
            phase.apply(builder),
            (mc, e) -> ModLoadingContext.get().setActiveContainer(mc),
            (mc, e) -> ModLoadingContext.get().setActiveContainer(null));

        for (var type : builder)
        {
            registeredBuffers.put(type, new BufferBuilder(type.bufferSize()));
        }
        
        renderTypes = ImmutableList.copyOf(registeredBuffers.keySet());
    }

    public ImmutableList<RenderType> getRenderTypes()
    {
        return renderTypes;
    }

    public ObjectCollection<BufferBuilder> getBuffers()
    {
        return registeredBuffers.values();
    }

    public BufferBuilder getBuilder(RenderType type)
    {
        return registeredBuffers.get(type);
    }
}
